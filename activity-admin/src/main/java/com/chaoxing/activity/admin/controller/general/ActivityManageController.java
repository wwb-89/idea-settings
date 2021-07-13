package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityFlagSignModule;
import com.chaoxing.activity.model.ActivitySignModule;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.org.OrgService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityManageController
 * @description
 * @blame wwb
 * @date 2020-12-29 17:08:04
 */
@Controller
@RequestMapping("activity")
public class ActivityManageController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityCreatePermissionService activityCreatePermissionService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private ActivityScopeQueryService activityScopeQueryService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private OrgService orgService;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-29 17:13:59
	 * @param model
	 * @param activityId
	 * @param request
	 * @return java.lang.String
	*/
	@RequestMapping("{activityId}")
	public String activityIndex(Model model, @PathVariable Integer activityId, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer operateUid = loginUser.getUid();
		Activity activity = activityValidationService.manageAble(activityId, operateUid);
		model.addAttribute("activity", activity);
		Integer signId = activity.getSignId();
		SignActivityManageIndexDTO signActivityManageIndex = signApiService.statSignActivityManageIndex(signId);
		model.addAttribute("signActivityManageIndex", signActivityManageIndex);
		// 是不是创建者
		boolean creator = activityValidationService.isCreator(activity, operateUid);
		model.addAttribute("isCreator", creator);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/activity-index";
		} else {
			return "pc/activity-index";
		}
	}

	/**活动修改页面
	 * @Description
	 * @author wwb
	 * @Date 2020-11-25 15:26:28
	 * @param model
	 * @param activityId
	 * @param request
	 * @param step
	 * @param strict
	 * @return java.lang.String
	 */
	@GetMapping("{activityId}/edit")
	public String edit(Model model, @PathVariable Integer activityId, HttpServletRequest request, Integer step, @RequestParam(defaultValue = "0") Integer strict) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
		model.addAttribute("activity", activity);
		// 活动类型列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		// 活动分类列表范围
		ActivityCreatePermissionDTO activityCreatePermission = activityCreatePermissionService.getGroupClassifyByUserPermission(loginUser.getFid(), loginUser.getUid());
		model.addAttribute("activityClassifies", activityCreatePermission.getActivityClassifies());
		model.addAttribute("existNoLimitPermission", activityCreatePermission.getExistNoLimitPermission());
		model.addAttribute("groupType", activityCreatePermission.getGroupType());
		// 报名签到
		Integer signId = activity.getSignId();
		SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getById(signId);
		}
		model.addAttribute("sign", sign);
		// 模板列表，如果选择过模版就查询选择的模版，否则查询可用的模版
		Integer webTemplateId = activity.getWebTemplateId();
		List<WebTemplate> webTemplates;
		if (webTemplateId != null) {
			webTemplates = Lists.newArrayList();
			WebTemplate webTemplate = webTemplateService.getById(webTemplateId);
			if (webTemplate != null) {
				webTemplates.add(webTemplate);
			}
		} else {
			webTemplates = webTemplateService.listAvailable(loginUser.getFid(), activity.getActivityFlag());
		}
		model.addAttribute("webTemplates", webTemplates);
		model.addAttribute("step", step);
		// 活动发布范围
		List<WfwAreaDTO> wfwRegionalArchitectures = activityScopeQueryService.listByActivityId(activityId);
		model.addAttribute("participatedOrgs", wfwRegionalArchitectures);
		// 报名范围
		model.addAttribute("wfwGroups", activityCreatePermission.getWfwGroups());
		String activityFlag = activity.getActivityFlag();
		model.addAttribute("activityFlag", activityFlag);
		// flag配置的报名签到的模块
		List<ActivityFlagSignModule> activityFlagSignModules = activityQueryService.listSignModuleByFlag(activityFlag);
		model.addAttribute("activityFlagSignModules", activityFlagSignModules);
		// 活动关联的报名签到模块列表
		List<ActivitySignModule> activitySignModules = activityQueryService.listByActivityId(activityId);
		model.addAttribute("activitySignModules", activitySignModules);
		model.addAttribute("strict", strict);
		// 是不是定制机构：定制机构不显示简介
		boolean customOrg = orgService.isCustomOrg(activity.getCreateFid());
		model.addAttribute("customOrg", customOrg);
		return "pc/activity-add-edit";
	}

}