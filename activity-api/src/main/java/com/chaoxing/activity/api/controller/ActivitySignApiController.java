package com.chaoxing.activity.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.mapper.ActivitySignModuleMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.component.ComponentQueryService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivitySignApiController
 * @description 活动市场版本刷数据使用
 * @blame wwb
 * @date 2021-07-15 15:51:07
 */
@Deprecated
@RestController
@RequestMapping("sign")
public class ActivitySignApiController {

	@Resource
	private ActivitySignModuleMapper activitySignModuleMapper;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private ComponentQueryService componentQueryService;

	@RequestMapping("{signId}/sign-up/{signUpId}/origin")
	public RestRespDTO getSignUpOriginId(@PathVariable Integer signId, @PathVariable Integer signUpId) {
		Integer templateComponentId = getTemplateComponentId(signId, signUpId, ActivityFlagSignModule.ModuleType.SIGN_UP, "sign_up");
		return RestRespDTO.success(templateComponentId);
	}

	private Integer getTemplateComponentId(Integer signId, Integer identify, ActivityFlagSignModule.ModuleType moduleType, String componentCode) {
		Activity activity = activityQueryService.getBySignId(signId);
		Integer activityId = activity.getId();
		List<ActivitySignModule> activitySignModules = activitySignModuleMapper.selectList(new LambdaQueryWrapper<ActivitySignModule>()
				.eq(ActivitySignModule::getActivityId, activityId)
				.eq(ActivitySignModule::getModuleType, moduleType.getValue())
				.eq(ActivitySignModule::getModuleId, identify)
		);
		if (CollectionUtils.isNotEmpty(activitySignModules)) {
			// 查询组件id
			Component component = componentQueryService.getSystemComponentByCode(componentCode);
			// 根据模版id和组件id查询模版组件id
			List<TemplateComponent> templateComponents = templateQueryService.listByTemplateIdAndComponentId(activity.getTemplateId(), component.getId());
			TemplateComponent templateComponent = Optional.ofNullable(templateComponents).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
			return Optional.ofNullable(templateComponent).map(TemplateComponent::getId).orElse(null);
		}
		return null;
	}

	@RequestMapping("{signId}/company-sign-up/{signUpId}/origin")
	public RestRespDTO getCompanySignUpOriginId(@PathVariable Integer signId, @PathVariable Integer signUpId) {
		Integer templateComponentId = getTemplateComponentId(signId, signUpId, ActivityFlagSignModule.ModuleType.SIGN_UP, "company_sign_up");
		return RestRespDTO.success(templateComponentId);
	}

	@RequestMapping("{signId}/sign-in/{signInId}/origin")
	public RestRespDTO getSignInOriginId(@PathVariable Integer signId, @PathVariable Integer signInId) {
		Integer templateComponentId = getTemplateComponentId(signId, signInId, ActivityFlagSignModule.ModuleType.SIGN_IN, "sign_in");
		return RestRespDTO.success(templateComponentId);
	}

	@RequestMapping("{signId}/sign-out/{signOutId}/origin")
	public RestRespDTO getSignOutOriginId(@PathVariable Integer signId, @PathVariable Integer signOutId) {
		Integer templateComponentId = getTemplateComponentId(signId, signOutId, ActivityFlagSignModule.ModuleType.SIGN_OUT, "sign_out");
		return RestRespDTO.success(templateComponentId);
	}

}