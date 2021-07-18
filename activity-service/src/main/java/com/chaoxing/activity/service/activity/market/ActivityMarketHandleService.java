package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAppCreateParamDTO;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMarket;
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
 * @className ActivityMarketHandleService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:13:02
 */
@Slf4j
@Service
public class ActivityMarketHandleService {

	@Resource
	private ActivityMarketMapper activityMarketMapper;

	@Resource
	private ActivityMarketQueryService activityMarketQueryService;
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
	public ActivityMarket add(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		ActivityMarket activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
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
	public ActivityMarket addFromWfw(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		ActivityMarket activityMarket = add(activityMarketCreateParamDto, operateUserDto);
		// 创建微服务应用
		addWfwApp(activityMarket, activityMarketCreateParamDto.getClassifyId());
		return activityMarket;
	}

	private void addWfwApp(ActivityMarket activityMarket, Integer classifyId) {
		// 应用
		WfwAppCreateParamDTO wfwAppCreateParamDto = WfwAppCreateParamDTO.buildFromActivityMarket(activityMarket, classifyId);
		wfwAppApiService.newApp(wfwAppCreateParamDto);
		// 应用管理
		WfwAppCreateParamDTO wfwManageAppCreateParamDto = WfwAppCreateParamDTO.buildManageAppFromActivityMarket(activityMarket, classifyId);
		wfwAppApiService.newApp(wfwManageAppCreateParamDto);
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
		ActivityMarket activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		activityMarketMapper.insert(activityMarket);
		Integer marketId = activityMarket.getId();
		// 给市场克隆一个通用模版
		templateHandleService.cloneTemplate(marketId, templateQueryService.getSystemTemplateIdByActivityFlag(activityFlagEnum));
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
		ActivityMarketCreateParamDTO activityMarketCreateParamDto = ActivityMarketCreateParamDTO.build(activityFlagEnum.getName(), ActivityMarket.DEFAULT_MARKET_ICON_CLOUD_ID, fid);
		ApplicationContextHolder.getBean(ActivityMarketHandleService.class).add(activityMarketCreateParamDto, activityFlagEnum, operateUserDto);
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
		ActivityMarket activityMarket = activityMarketUpdateParamDto.buildActivityMarket();
		Integer marketId = activityMarket.getId();
		Optional.ofNullable(activityMarketQueryService.getById(marketId)).orElseThrow(() -> new BusinessException("活动市场不存在"));
		activityMarket.updateValidate(operateUserDto);
		activityMarketMapper.update(activityMarket, new LambdaUpdateWrapper<ActivityMarket>()
			.eq(ActivityMarket::getId, activityMarket.getId())
		);
	}

}