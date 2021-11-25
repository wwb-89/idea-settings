package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.activity.market.MarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.MarketUpdateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAppParamDTO;
import com.chaoxing.activity.mapper.MarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityMarketService;
import com.chaoxing.activity.service.activity.template.TemplateHandleService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwAppApiService;
import com.chaoxing.activity.util.ApplicationContextHolder;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketHandleService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:13:02
 */
@Slf4j
@Service
public class MarketHandleService {

	@Resource
	private MarketMapper marketMapper;

	@Resource
	private MarketQueryService marketQueryService;
	@Resource
	private TemplateHandleService templateHandleService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private WfwAppApiService wfwAppApiService;
	@Resource
	private ActivityMarketService activityMarketService;
	@Resource
	private ActivityHandleService activityHandleService;
	@Resource
	private MarketSignupConfigService marketSignupConfigService;


	/**单独创建活动市场
	* @Description
	* @author huxiaolong
	* @Date 2021-08-25 15:32:59
	* @param activityMarketCreateParamDto
	* @param operateUserDto
	* @return com.chaoxing.activity.model.Market
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market createMarket(MarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		Market activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		activityMarket.perfectSequence(marketMapper.getMaxSequence(operateUserDto.getFid()));
		add(activityMarket);
		return activityMarket;
	}

	private void add(Market market) {
		marketMapper.insert(market);
		// 活动市场报名配置
		marketSignupConfigService.init(market.getId());
	}

	/**创建活动市场且克隆一个模板
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:34:09
	 * @param marketCreateParamDto
	 * @param operateUserDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market add(MarketCreateParamDTO marketCreateParamDto, OperateUserDTO operateUserDto) {
		Market market = createMarket(marketCreateParamDto, operateUserDto);
		// 给市场克隆一个模版
		String activityFlag = marketCreateParamDto.getActivityFlag();
		// 没有指定模版就使用系统的normal
		Activity.ActivityFlagEnum activityFlagEnum = Optional.ofNullable(Activity.ActivityFlagEnum.fromValue(activityFlag)).orElse(Activity.ActivityFlagEnum.NORMAL);
		templateHandleService.cloneTemplate(market, templateQueryService.getSystemTemplateIdByActivityFlag(activityFlagEnum));
		return market;
	}

	/**创建活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 17:11:35
	 * @param marketCreateParamDto
	 * @param operateUserDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market addFromWfw(MarketCreateParamDTO marketCreateParamDto, OperateUserDTO operateUserDto) {
		marketCreateParamDto.setOriginType(Market.OriginTypeEnum.WFW.getValue());
		Market market = add(marketCreateParamDto, operateUserDto);
		// 创建微服务应用
		Integer wfwAppId = addWfwApp(market, marketCreateParamDto.getClassifyId());
		// 绑定应用的微服务id
		market.bindWfwApp(wfwAppId);
		update(MarketUpdateParamDTO.buildFromActivityMarket(market));
		return market;
	}

	private Integer addWfwApp(Market activityMarket, Integer classifyId) {
		WfwAppParamDTO wfwAppParamDTO = WfwAppParamDTO.buildFromActivityMarket(activityMarket, classifyId);
		return wfwAppApiService.newApp(wfwAppParamDTO);
	}

	/**修改活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-21 14:46:31
	 * @param marketUpdateParamDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market updateFromWfw(MarketUpdateParamDTO marketUpdateParamDto) {
		Market activityMarket = marketUpdateParamDto.buildActivityMarket();
		marketMapper.updateById(activityMarket);
		// 修改微服务应用
		updateWfwApp(activityMarket, marketUpdateParamDto.getClassifyId());
		return activityMarket;
	}

	private void updateWfwApp(Market activityMarket, Integer classifyId) {
		WfwAppParamDTO wfwAppParamDTO = WfwAppParamDTO.buildFromActivityMarket(activityMarket, classifyId);
		wfwAppApiService.updateApp(wfwAppParamDTO);
	}

	/**创建活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 20:38:43
	 * @param marketCreateParamDto
	 * @param activityFlagEnum
	 * @param operateUserDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market add(MarketCreateParamDTO marketCreateParamDto, Activity.ActivityFlagEnum activityFlagEnum, OperateUserDTO operateUserDto) {
		Market activityMarket = marketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		add(activityMarket);
		// 给市场克隆一个模版
		templateHandleService.cloneTemplate(activityMarket, templateQueryService.getSystemTemplateIdByActivityFlag(activityFlagEnum));
		return activityMarket;
	}

	/**更新活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-21 11:49:56
	 * @param marketUpdateParamDto
	 * @return void
	*/
	public void update(MarketUpdateParamDTO marketUpdateParamDto) {
		Market activityMarket = marketUpdateParamDto.buildActivityMarket();
		marketMapper.updateById(activityMarket);
	}

	/**创建活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 20:33:29
	 * @param fid
	 * @param activityFlag
	 * @param operateUserDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(Integer fid, String activityFlag, OperateUserDTO operateUserDto) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(activityFlag);
		MarketCreateParamDTO activityMarketCreateParamDto = MarketCreateParamDTO.build(fid, null, activityFlag);
		activityMarketCreateParamDto.setName(activityFlagEnum.getName());
		ApplicationContextHolder.getBean(MarketHandleService.class).add(activityMarketCreateParamDto, activityFlagEnum, operateUserDto);
	}

	/**更新活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:47:43
	 * @param marketUpdateParamDto
	 * @param operateUserDto
	 * @return void
	*/
	public void update(MarketUpdateParamDTO marketUpdateParamDto, OperateUserDTO operateUserDto) {
		Market activityMarket = marketUpdateParamDto.buildActivityMarket();
		Integer marketId = activityMarket.getId();
		Optional.ofNullable(marketQueryService.getById(marketId)).orElseThrow(() -> new BusinessException("活动市场不存在"));
		activityMarket.updateValidate(operateUserDto);
		marketMapper.update(activityMarket, new LambdaUpdateWrapper<Market>()
			.eq(Market::getId, activityMarket.getId())
		);
	}

	/**根据机构id，活动标识查询模板,判断市场是否存在; 市场不存在则创建市场
	 * 返回市场id
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-25 14:39:27
	 * @param fid
	 * @param activityFlagEnum
	 * @return com.chaoxing.activity.model.Template
	 */
	public Integer getOrCreateOrgMarket(Integer fid, Activity.ActivityFlagEnum activityFlagEnum, LoginUserDTO loginUser) {
		if (activityFlagEnum == null) {
			throw new BusinessException("未知的flag");
		}
		Integer marketId = marketQueryService.getMarketIdByFlag(fid, activityFlagEnum.getValue());
		if (marketId == null) {
			// 创建一个活动市场
			Market market = ApplicationContextHolder.getBean(MarketHandleService.class).add(MarketCreateParamDTO.build(fid, null, activityFlagEnum.getValue()), activityFlagEnum, loginUser.buildOperateUserDTO());
			marketId = market.getId();
		}
		return marketId;
	}

	/**克隆市场和模板
	* @Description
	* @author huxiaolong
	* @Date 2021-09-06 18:21:34
	* @param originMarketId
	* @param originTemplateId
	* @param targetFid
	* @param loginUser
	* @return com.chaoxing.activity.model.Market
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market cloneMarketAndTemplate(Integer originMarketId, Integer originTemplateId, Integer targetFid, LoginUserDTO loginUser) {
		Market originMarket = marketQueryService.getById(originMarketId);

		Market newMarket = Market.cloneMarket(originMarket, targetFid);
		newMarket.perfectCreator(loginUser.buildOperateUserDTO());
		add(newMarket);

		// 给市场克隆一个模版
		templateHandleService.cloneTemplate(newMarket, originTemplateId);
		return newMarket;
	}

	/**根据市场wfwAppId删除市场，及市场关联的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-01 16:52:19
	 * @param wfwAppId
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
    public void deleteByWfwAppId(Integer wfwAppId) {
		Market market = marketMapper.selectList(new LambdaQueryWrapper<Market>()
				.eq(Market::getOriginType, Market.OriginTypeEnum.WFW.getValue())
				.eq(Market::getOrigin, String.valueOf(wfwAppId))
		).stream().findFirst().orElse(null);
		if (market == null) {
			return;
		}
		Integer marketId = market.getId();
		// 手动删除市场
		marketMapper.update(null, new LambdaUpdateWrapper<Market>()
				.eq(Market::getId, marketId)
				.set(Market::getDeleted, true));
		// 删除市场下的模板
		templateHandleService.deleteByMarketId(marketId);
		// 删除市场下关联的活动
		activityMarketService.deleteByMarketId(marketId);
		// 删除市场id为marketId的活动
		activityHandleService.deleteByMarketId(marketId);
    }
}