package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMenuConfig;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
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
import java.util.stream.Collectors;

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
	private ActivityEngineQueryService activityEngineQueryService;
	@Resource
	private ActivityMenuService activityMenuService;

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
		model.addAttribute("activityMenus", activityMenuService.listActivityMenuConfig(activityId).stream().map(ActivityMenuConfig::getMenu).collect(Collectors.toList()));
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
	 * @param strict
	 * @return java.lang.String
	 */
	@GetMapping("{activityId}/edit")
	public String edit(Model model, @PathVariable Integer activityId, HttpServletRequest request, @RequestParam(defaultValue = "0") Integer strict) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
		ActivityCreateParamDTO createParamDTO = activityQueryService.packageActivityCreateParamByActivity(activity);
		model.addAttribute("activity", createParamDTO);
		model.addAttribute("templateComponents", activityEngineQueryService.listTemplateComponentTree(activity.getTemplateId(), activity.getCreateFid()));
		// 活动类型列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		// 当前用户创建活动权限
		ActivityCreatePermissionDTO permission = activityCreatePermissionService.getActivityCreatePermission(loginUser.getFid(), activity.getMarketId(), loginUser.getUid());
		model.addAttribute("activityClassifies", permission.getClassifies());
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
		// 活动发布范围
		List<WfwAreaDTO> wfwRegionalArchitectures = activityScopeQueryService.listByActivityId(activityId);
		model.addAttribute("participatedOrgs", wfwRegionalArchitectures);
		// 报名范围
		// 微服务组织架构
		model.addAttribute("wfwGroups", permission.getWfwGroups());
		// 通讯录组织架构
		model.addAttribute("contactGroups", permission.getContactsGroups());
		String activityFlag = activity.getActivityFlag();
		model.addAttribute("activityFlag", activityFlag);
		model.addAttribute("strict", strict);
		return "pc/activity-add-edit-new";
	}

}