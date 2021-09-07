package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAppParamDTO;
import com.chaoxing.activity.mapper.MarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.model.Template;
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
	private MarketValidationService marketValidationService;


	/**单独创建活动市场
	* @Description
	* @author huxiaolong
	* @Date 2021-08-25 15:32:59
	* @param activityMarketCreateParamDto
	* @param operateUserDto
	* @return com.chaoxing.activity.model.Market
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market createMarket(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		Market activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		activityMarket.perfectSequence(marketMapper.getMaxSequence(operateUserDto.getFid()));
		marketMapper.insert(activityMarket);
		return activityMarket;
	}

	/**创建活动市场且克隆一个通用模板
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:34:09
	 * @param activityMarketCreateParamDto
	 * @param operateUserDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market add(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		Market activityMarket = ApplicationContextHolder.getBean(MarketHandleService.class).createMarket(activityMarketCreateParamDto, operateUserDto);
		Integer marketId = activityMarket.getId();
		// 给市场克隆一个模版
		String activityFlag = activityMarketCreateParamDto.getActivityFlag();
		Activity.ActivityFlagEnum activityFlagEnum = Optional.ofNullable(Activity.ActivityFlagEnum.fromValue(activityFlag)).orElse(Activity.ActivityFlagEnum.NORMAL);
		templateHandleService.cloneTemplate(marketId, templateQueryService.getSystemTemplateIdByActivityFlag(activityFlagEnum));
		return activityMarket;
	}

	/**创建活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 17:11:35
	 * @param activityMarketCreateParamDto
	 * @param operateUserDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market addFromWfw(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		Market market = add(activityMarketCreateParamDto, operateUserDto);
		// 创建微服务应用
		Integer wfwAppId = addWfwApp(market, activityMarketCreateParamDto.getClassifyId());
		// 绑定应用的微服务id
		market.bindWfwApp(wfwAppId);
		update(ActivityMarketUpdateParamDTO.buildFromActivityMarket(market));
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
	 * @param activityMarketUpdateParamDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market updateFromWfw(ActivityMarketUpdateParamDTO activityMarketUpdateParamDto) {
		Market activityMarket = activityMarketUpdateParamDto.buildActivityMarket();
		marketMapper.updateById(activityMarket);
		// 修改微服务应用
		updateWfwApp(activityMarket, activityMarketUpdateParamDto.getClassifyId());
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
	 * @param activityMarketCreateParamDto
	 * @param activityFlagEnum
	 * @param operateUserDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market add(ActivityMarketCreateParamDTO activityMarketCreateParamDto, Activity.ActivityFlagEnum activityFlagEnum, OperateUserDTO operateUserDto) {
		Market activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		marketMapper.insert(activityMarket);
		Integer marketId = activityMarket.getId();
		// 给市场克隆一个模版
		templateHandleService.cloneTemplate(marketId, templateQueryService.getSystemTemplateIdByActivityFlag(activityFlagEnum));
		return activityMarket;
	}

	/**更新活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-21 11:49:56
	 * @param activityMarketUpdateParamDto
	 * @return void
	*/
	public void update(ActivityMarketUpdateParamDTO activityMarketUpdateParamDto) {
		Market activityMarket = activityMarketUpdateParamDto.buildActivityMarket();
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
		ActivityMarketCreateParamDTO activityMarketCreateParamDto = ActivityMarketCreateParamDTO.build(fid, null);
		activityMarketCreateParamDto.setName(activityFlagEnum.getName());
		ApplicationContextHolder.getBean(MarketHandleService.class).add(activityMarketCreateParamDto, activityFlagEnum, operateUserDto);
	}

	/**更新活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:47:43
	 * @param activityMarketUpdateParamDto
	 * @param operateUserDto
	 * @return void
	*/
	public void update(ActivityMarketUpdateParamDTO activityMarketUpdateParamDto, OperateUserDTO operateUserDto) {
		Market activityMarket = activityMarketUpdateParamDto.buildActivityMarket();
		Integer marketId = activityMarket.getId();
		Optional.ofNullable(marketQueryService.getById(marketId)).orElseThrow(() -> new BusinessException("活动市场不存在"));
		activityMarket.updateValidate(operateUserDto);
		marketMapper.update(activityMarket, new LambdaUpdateWrapper<Market>()
			.eq(Market::getId, activityMarket.getId())
		);
	}

	/**更新同时报名活动数量限制
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-18 16:16:46
	 * @param marketId
	 * @param signUpActivityLimit
	 * @param operateUserDto
	 * @return void
	*/
	public void updateSignUpActivityLimit(Integer marketId, Integer signUpActivityLimit, OperateUserDTO operateUserDto) {
		marketValidationService.manageAble(marketId, operateUserDto);
		marketMapper.update(null, new LambdaUpdateWrapper<Market>()
				.eq(Market::getId, marketId)
				.set(Market::getSignUpActivityLimit, signUpActivityLimit)
		);
	}


	/**根据机构id，活动标识查询模板,判断市场是否存在; 模板不存在则创建模板，市场不存在则创建市场
	 * 返回模板(模板id, 市场id)
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-25 14:39:27
	 * @param fid
	 * @param activityFlagEnum
	 * @return com.chaoxing.activity.model.Template
	 */
	@Transactional(rollbackFor = Exception.class)
	public Template getOrCreateTemplateMarketByFidActivityFlag(Integer fid, Activity.ActivityFlagEnum activityFlagEnum, LoginUserDTO loginUserDTO) {
		if (activityFlagEnum == null) {
			throw new BusinessException("未知的flag");
		}
		Template template = templateQueryService.getOrgTemplateByActivityFlag(fid, activityFlagEnum);
		ActivityMarketCreateParamDTO marketCreateParam = ActivityMarketCreateParamDTO.builder().name(activityFlagEnum.getName().concat("活动市场")).fid(fid).build();
		if (template != null) {
			// 若有模板无市场，则建立对应市场
			if (template.getMarketId() == null) {
				Market market = ApplicationContextHolder.getBean(MarketHandleService.class).createMarket(marketCreateParam, loginUserDTO.buildOperateUserDTO());
				template.setMarketId(market.getId());
				templateHandleService.update(template);
			}
			return template;
		}
		// 如果不存在fid对应的模板，证明无对应市场，先创建市场
		ApplicationContextHolder.getBean(MarketHandleService.class).add(marketCreateParam, activityFlagEnum, loginUserDTO.buildOperateUserDTO());
		return templateQueryService.getOrgTemplateByActivityFlag(fid, activityFlagEnum);
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
		marketMapper.insert(newMarket);

		// 给市场克隆一个模版
		templateHandleService.cloneTemplate(newMarket.getId(), originTemplateId);
		return newMarket;
	}

}