package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAppParamDTO;
import com.chaoxing.activity.mapper.MarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Market;
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
	private MarketMapper activityMarketMapper;

	@Resource
	private MarketQueryService activityMarketQueryService;
	@Resource
	private TemplateHandleService templateHandleService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private WfwAppApiService wfwAppApiService;

	/**创建活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:34:09
	 * @param activityMarketCreateParamDto
	 * @param operateUserDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	@Transactional(rollbackFor = Exception.class)
	public Market add(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		Market activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		activityMarket.perfectSequence(activityMarketMapper.getMaxSequence(operateUserDto.getFid()));
		activityMarketMapper.insert(activityMarket);
		Integer marketId = activityMarket.getId();
		// 给市场克隆一个通用模版
		templateHandleService.cloneTemplate(marketId, templateQueryService.getSystemTemplateIdByActivityFlag(Activity.ActivityFlagEnum.NORMAL));
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
		Market activityMarket = add(activityMarketCreateParamDto, operateUserDto);
		// 创建微服务应用
		Integer wfwAppId = addWfwApp(activityMarket, activityMarketCreateParamDto.getClassifyId());
		// 绑定应用的微服务id
		activityMarket.bindWfwApp(wfwAppId);
		update(ActivityMarketUpdateParamDTO.buildFromActivityMarket(activityMarket));
		return activityMarket;
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
		activityMarketMapper.updateById(activityMarket);
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
	public void add(ActivityMarketCreateParamDTO activityMarketCreateParamDto, Activity.ActivityFlagEnum activityFlagEnum, OperateUserDTO operateUserDto) {
		Market activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		activityMarketMapper.insert(activityMarket);
		Integer marketId = activityMarket.getId();
		// 给市场克隆一个通用模版
		templateHandleService.cloneTemplate(marketId, templateQueryService.getSystemTemplateIdByActivityFlag(activityFlagEnum));
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
		activityMarketMapper.updateById(activityMarket);
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
		Optional.ofNullable(activityMarketQueryService.getById(marketId)).orElseThrow(() -> new BusinessException("活动市场不存在"));
		activityMarket.updateValidate(operateUserDto);
		activityMarketMapper.update(activityMarket, new LambdaUpdateWrapper<Market>()
			.eq(Market::getId, activityMarket.getId())
		);
	}

}