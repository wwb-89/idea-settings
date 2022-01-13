package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.ConditionDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.module.ClazzInteractionDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.classify.component.ClassifyShowComponentQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.engine.CustomAppConfigQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.scope.ActivityClassService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.tag.TagQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.vo.manager.WfwFormFieldVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
@Slf4j
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
	private TemplateComponentService templateComponentService;
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
	@Resource
	private MarketSignupConfigService marketSignupConfigService;
	@Resource
	private TagQueryService tagQueryService;
	@Resource
	private CustomAppConfigQueryService customAppConfigQueryService;
	@Resource
	private ClassifyShowComponentQueryService classifyShowComponentQueryService;

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
//		todo 暂时屏蔽校验
//		Activity activity = activityValidationService.manageAble(activityId, operateUid);
		Activity activity = activityValidationService.activityExist(activityId);
		model.addAttribute("activity", activity);
		Integer signId = activity.getSignId();
		SignActivityManageIndexDTO signActivityManageIndex = signApiService.statSignActivityManageIndex(signId);
		model.addAttribute("signActivityManageIndex", signActivityManageIndex);
		// 是不是创建者
		boolean creator = activityValidationService.isCreator(activity, loginUser.getUid());
		List<ActivityMenuDTO> activityMenuConfigs = activityMenuService.listUserActivityMenus(activity, loginUser, creator);
		model.addAttribute("isCreator", creator);
		model.addAttribute("activityMenuConfigs", activityMenuConfigs);
		model.addAttribute("signWebDomain", DomainConstant.SIGN_WEB);
		model.addAttribute("xiamenTrainingPlatformDomain", DomainConstant.XIAMEN_TRAINING_PLATFORM_API);
		model.addAttribute("mainDomain", DomainConstant.MAIN);
		model.addAttribute("webDomain", DomainConstant.WEB);
		model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
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
	public String edit(Model model, @PathVariable Integer activityId, HttpServletRequest request, String areaCode, @RequestParam(defaultValue = "0") Integer strict) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityValidationService.editAble(activityId, loginUser);
		ActivityCreateParamDTO createParam = activityQueryService.packageActivityCreateParamByActivity(activity);
		model.addAttribute("activity", createParam);
		// 报名签到
		Integer signId = activity.getSignId();
		SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getCreateById(signId);
		}
		model.addAttribute("sign", sign);
		return editView(model,
				activityId,
				loginUser.getFid(),
				activity.getCreateFid(),
				activity.getMarketId(),
				loginUser.getUid(),
				activity.getActivityFlag(),
				activity.getTemplateId(),
				activity.getWebTemplateId(),
				areaCode,
				strict);
	}

	/**活动复制
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-18 14:40:22
	 * @param model
	 * @param activityId
	 * @param request
	 * @param strict
	 * @return java.lang.String
	 */
	@GetMapping("{activityId}/clone")
	public String clone(Model model, @PathVariable Integer activityId, HttpServletRequest request, @RequestParam(defaultValue = "0") Integer strict) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityQueryService.getById(activityId);
		ActivityCreateParamDTO createParam = activityQueryService.cloneActivity(activityId);
		model.addAttribute("isClone", true);
		model.addAttribute("activity", createParam);
		// 报名签到
		Integer signId = activity.getSignId();
		SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getCloneSign(signId, loginUser.getFid(), loginUser.getUid());
		}
		model.addAttribute("sign", sign);
		return editView(model,
				activityId,
				loginUser.getFid(),
				createParam.getCreateFid(),
				createParam.getMarketId(),
				loginUser.getUid(),
				createParam.getActivityFlag(),
				createParam.getTemplateId(),
				createParam.getWebTemplateId(),
				null,
				strict);
	}

	/**活动修改页面
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-24 14:22:27
	 * @param model
	 * @param activityId
	 * @param userFid
	 * @param activityFid
	 * @param marketId
	 * @param uid
	 * @param activityFlag
	 * @param templateId
	 * @param webTemplateId
	 * @param areaCode
	 * @param strict
	 * @return java.lang.String
	*/
	private String editView(Model model, Integer activityId, Integer userFid, Integer activityFid, Integer marketId, Integer uid, String activityFlag, Integer templateId, Integer webTemplateId, String areaCode, Integer strict) {
		// 活动对应的模板组件列表
		model.addAttribute("templateComponents", templateComponentService.listTemplateComponentTree(templateId, activityFid));
		// 活动类型列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		model.addAttribute("activityFlag", activityFlag);
		// 当前用户创建活动权限
		ActivityCreatePermissionDTO permission = activityCreatePermissionService.getActivityCreatePermission(userFid, marketId, uid);
		model.addAttribute("activityClassifies", classifyQueryService.classifiesUnionAreaClassifies(activityFlag, areaCode, permission.getClassifies()));
		// 模板列表，使用的模版和可选的模版
		WebTemplate usedWebTemplate = Optional.ofNullable(webTemplateId).map(v -> webTemplateService.getById(v)).orElse(null);
		model.addAttribute("usedWebTemplate", usedWebTemplate);
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(userFid, activityFlag);
		model.addAttribute("webTemplates", webTemplates);
		// 活动发布班级id集合
		List<Integer> releaseClassIds = activityClassService.listClassIdsByActivity(activityId);
		model.addAttribute("releaseClassIds", releaseClassIds);
		// 活动发布范围
		List<WfwAreaDTO> wfwRegionalArchitectures = activityScopeQueryService.listByActivityId(activityId);
		model.addAttribute("participatedOrgs", wfwRegionalArchitectures);
		// 微服务组织架构
		model.addAttribute("wfwGroups", permission.getWfwGroups());
		// 通讯录组织架构
		model.addAttribute("contactGroups", permission.getContactsGroups());
		model.addAttribute("strict", strict);
		List<SignUpCondition> signUpConditions = signUpConditionService.listEditActivityConditions(activityId, templateId);
		model.addAttribute("signUpConditions", signUpConditions);
		// 获取表单结构map
		List<String> formIds = signUpConditions.stream().map(SignUpCondition::getOriginIdentify).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
		Map<String, List<WfwFormFieldVO>> formFieldStructures = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(formIds)) {
			formFieldStructures = formIds.stream().collect(Collectors.toMap(
					v -> v,
					v -> formApiService.getFormStructure(Integer.valueOf(v), activityFid)
							.stream().map(WfwFormFieldVO::buildFromWfwFormFieldDTO)
							.collect(Collectors.toList()),
					(v1, v2) -> v2));
		}
		model.addAttribute("formFieldStructures", formFieldStructures);
		model.addAttribute("sucTplComponentIds", signUpConditionService.listActivityEnabledTemplateComponentId(activityId));
		model.addAttribute("conditionEnums", ConditionDTO.list());
		// 活动市场报名配置
		MarketSignUpConfig marketSignUpConfig = marketSignupConfigService.get(marketId);
		model.addAttribute("marketSignUpConfig", marketSignUpConfig);
		// 活动标签
		List<Tag> tags = Optional.ofNullable(marketId).map(v -> tagQueryService.listMarketTag(marketId)).orElse(tagQueryService.listOrgTag(activityFid));
		model.addAttribute("tags", tags);
		// 查询出活动分类关联的模板组建id列表
		List<ClassifyShowComponent> classifyShowComponents = classifyShowComponentQueryService.listByTemplateId(templateId);
		model.addAttribute("classifyShowComponents", classifyShowComponents);

		// 启用的自定义应用列表
		List<Integer> enableCustomAppTplComponentIds = customAppConfigQueryService.listEnabledActivityCustomAppTplComponentId(activityId);
		model.addAttribute("customAppEnableTplComponentIds", enableCustomAppTplComponentIds);

		model.addAttribute("workDomain", DomainConstant.WORK);
		model.addAttribute("xueyaDomain", DomainConstant.XUEYA);
		model.addAttribute("noteDomain", DomainConstant.NOTE);
		model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
		model.addAttribute("mainDomain", DomainConstant.MAIN);
		model.addAttribute("signWebDomain", DomainConstant.SIGN_WEB);
		model.addAttribute("wfwFormDomain", DomainConstant.WFW_FORM_API);
		return "pc/activity-add-edit-new";
	}

}