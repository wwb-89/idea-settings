package com.chaoxing.activity.admin.controller.general;

import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.ConditionDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerValidationService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.scope.ActivityClassService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.vo.manager.WfwFormFieldVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
	private ActivityManagerValidationService activityManagerValidationService;
	@Resource
	private TemplateComponentService templateComponentService;
	@Resource
	private ActivityManagerService activityManagerService;
	@Resource
	private ActivityMenuService activityMenuService;
	@Resource
	private ActivityClassService activityClassService;
	@Resource
	private ClassifyQueryService classifyQueryService;
	@Resource
	private SignUpConditionService signUpConditionService;
	@Resource
	private WfwFormApiService formApiService;

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
//		todo 暂时屏蔽校验
//		Activity activity = activityValidationService.manageAble(activityId, operateUid);
		Activity activity = activityValidationService.activityExist(activityId);
		model.addAttribute("activity", activity);
		Integer signId = activity.getSignId();
		SignActivityManageIndexDTO signActivityManageIndex = signApiService.statSignActivityManageIndex(signId);
		model.addAttribute("signActivityManageIndex", signActivityManageIndex);
		// 是不是创建者
		boolean creator = activityValidationService.isCreator(activity, operateUid);
		List<String> activityMenus = Lists.newArrayList();
		// todo 特殊情况，非管理人员获取当前活动所有菜单
		if (!activityManagerValidationService.isManager(activityId, operateUid) || creator) {
			// 创建者获取活动所有菜单
			activityMenus = activityMenuService.listActivityMenuConfig(activityId).stream()
					.map(ActivityMenuConfig::getMenu).collect(Collectors.toList());
		} else {
			ActivityManager activityManager = activityManagerService.getByActivityUid(activityId, operateUid);
			if (activityManager != null && StringUtils.isNotBlank(activityManager.getMenu())) {
				List<String> managerMenus = Arrays.asList(StringUtils.split(activityManager.getMenu(), ","));
				activityMenus = ActivityMenuDTO.buildFromActivityMenus(managerMenus)
						.stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList());
			}
		}
		model.addAttribute("isCreator", creator);
		model.addAttribute("activityMenus", activityMenus);
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
		model.addAttribute("templateComponents", templateComponentService.listTemplateComponentTree(activity.getTemplateId(), activity.getCreateFid()));
		// 活动类型列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		String activityFlag = activity.getActivityFlag();
		model.addAttribute("activityFlag", activityFlag);
		// 当前用户创建活动权限
		ActivityCreatePermissionDTO permission = activityCreatePermissionService.getActivityCreatePermission(loginUser.getFid(), activity.getMarketId(), loginUser.getUid());
		model.addAttribute("activityClassifies", classifyQueryService.classifiesUnionAreaClassifies(createParamDTO.getMarketId(), activityFlag, permission.getClassifies()));
		// 报名签到
		Integer signId = activity.getSignId();
		SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getCreateById(signId);
		}
		model.addAttribute("sign", sign);
		// 模板列表，使用的模版和可选的模版
		Integer webTemplateId = activity.getWebTemplateId();
		WebTemplate usedWebTemplate = Optional.ofNullable(webTemplateId).map(v -> webTemplateService.getById(v)).orElse(null);
		model.addAttribute("usedWebTemplate", usedWebTemplate);
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(loginUser.getFid(), activityFlag);
		model.addAttribute("webTemplates", webTemplates);
		// 活动发布班级id集合
		List<Integer> releaseClassIds = activityClassService.listClassIdsByActivity(activityId);
		model.addAttribute("releaseClassIds", releaseClassIds);
		// 活动发布范围
		List<WfwAreaDTO> wfwRegionalArchitectures = activityScopeQueryService.listByActivityId(activityId);
		model.addAttribute("participatedOrgs", wfwRegionalArchitectures);
		// 报名范围
		// 微服务组织架构
		model.addAttribute("wfwGroups", permission.getWfwGroups());
		// 通讯录组织架构
		model.addAttribute("contactGroups", permission.getContactsGroups());
		model.addAttribute("strict", strict);
		List<SignUpCondition> signUpConditions = signUpConditionService.listEditActivityConditions(activityId, activity.getTemplateId());
		// 获取表单结构map
		List<String> formIds = signUpConditions.stream().map(SignUpCondition::getOriginIdentify).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
		Map<String, List<WfwFormFieldVO>> formFieldStructures = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(formIds)) {
			formFieldStructures = formIds.stream().collect(Collectors.toMap(
					v -> v,
					v -> formApiService.getFormStructure(Integer.valueOf(v), activity.getCreateFid())
							.stream().map(WfwFormFieldVO::buildFromWfwFormFieldDTO)
							.collect(Collectors.toList()),
					(v1, v2) -> v2));
		}
		model.addAttribute("formFieldStructures", formFieldStructures);
		model.addAttribute("sucTplComponentIds", signUpConditionService.listActivityEnabledTemplateComponentId(activityId));
		model.addAttribute("signUpConditions", signUpConditions);
		model.addAttribute("conditionEnums", ConditionDTO.list());
		return "pc/activity-add-edit-new";
	}

}