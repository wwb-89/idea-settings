package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.ConditionDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.vo.manager.WfwFormFieldVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className AbsActivityManageController
 * @description
 * @blame wwb
 * @date 2020-12-25 10:13:11
 */
@Component
public class ActivityController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private TableFieldQueryService tableFieldQueryService;
	@Resource
	private TemplateComponentService templateComponentService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private ActivityCreatePermissionService activityCreatePermissionService;
	@Resource
	private ClassifyQueryService classifyQueryService;
	@Resource
	private SignUpConditionService signUpConditionService;
	@Resource
	private WfwFormApiService formApiService;

	/**新活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 15:32:59
	 * @param model
	 * @param marketId
	 * @param fid 空间或微服务后台进入时查询的活动以该fid为主
	 * @param strict 是不是严格模式， 严格模式：只显示自己创建的活动
	 * @param flag
	 * @return java.lang.String
	*/
	public String index(Model model, Integer marketId, Integer fid, Integer strict, String flag, Integer pageMode) {
		model.addAttribute("fid", fid);
		model.addAttribute("strict", strict);
		model.addAttribute("marketId", marketId);
		model.addAttribute("flag", flag);
		if (Objects.equals(pageMode, 1)) {
			return "pc/activity-list-simple";
		}
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		List<MarketTableField> marketTableFields = tableFieldQueryService.listMarketTableField(marketId, TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		Integer tableFieldId = Optional.ofNullable(tableFieldDetails).orElse(Lists.newArrayList()).stream().findFirst().map(TableFieldDetail::getTableFieldId).orElse(null);
		List<Classify> classifies;
		if (marketId == null) {
			classifies = classifyQueryService.listOrgClassifies(fid);
		} else {
			classifies = classifyQueryService.listMarketClassifies(marketId);
		}
		model.addAttribute("classifies", classifies);
		model.addAttribute("tableFieldId", tableFieldId);
		model.addAttribute("tableFieldDetails", tableFieldDetails);
		model.addAttribute("marketTableFields", marketTableFields);
		return "pc/activity-list";
	}

	public String add(HttpServletRequest request, Model model, Integer marketId, String flag, Integer strict) {
		flag = Optional.ofNullable(flag).filter(StringUtils::isNotBlank).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		Template template = templateQueryService.getTemplateByMarketIdOrActivityFlag(marketId, Activity.ActivityFlagEnum.fromValue(flag));
		Integer templateId = Optional.ofNullable(template).map(Template::getId).orElse(null);
		model.addAttribute("marketId", marketId);
		model.addAttribute("strict", strict);
		model.addAttribute("templateId", template.getId());
		// 加载模版对应的组件列表
		model.addAttribute("templateComponents", templateComponentService.listTemplateComponentTree(templateId, template.getFid()));
		// 活动形式列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		// 活动分类列表范围
		// 当前用户创建活动权限
		ActivityCreatePermissionDTO permission = activityCreatePermissionService.getActivityCreatePermission(fid, marketId, loginUser.getUid());

		model.addAttribute("activityClassifies", classifyQueryService.classifiesUnionAreaClassifies(marketId, flag, permission.getClassifies()));
		// 报名签到
		model.addAttribute("sign", SignCreateParamDTO.builder().build());
		flag = Optional.of(template).map(Template::getActivityFlag).orElse(flag);
		// 模板列表
		model.addAttribute("webTemplates", webTemplateService.listAvailable(fid, flag));
		// 微服务组织架构
		model.addAttribute("wfwGroups", permission.getWfwGroups());
		// 通讯录组织架构
		model.addAttribute("contactGroups", permission.getContactsGroups());
		model.addAttribute("activityFlag", flag);
		// 发布范围默认选中当前机构
		List<WfwAreaDTO> wfwAreaDtos = wfwAreaApiService.listByFid(fid);
		List<WfwAreaDTO> participatedOrgs = Optional.ofNullable(wfwAreaDtos).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).collect(Collectors.toList());
		model.addAttribute("participatedOrgs", participatedOrgs);

		List<SignUpCondition> signUpConditions = signUpConditionService.listWithActivityConditionsByTemplate(templateId);
		// 获取表单结构map
		List<String> formIds = signUpConditions.stream().map(SignUpCondition::getOriginIdentify).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
		Map<String, List<WfwFormFieldVO>> formFieldStructures = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(formIds)) {
			formFieldStructures = formIds.stream().collect(Collectors.toMap(
					v -> v,
					v -> formApiService.getFormStructure(Integer.valueOf(v), fid)
							.stream().map(WfwFormFieldVO::buildFromWfwFormFieldDTO)
							.collect(Collectors.toList()),
					(v1, v2) -> v2));
		}
		model.addAttribute("formFieldStructures", formFieldStructures);
		model.addAttribute("signUpConditions", signUpConditions);
		model.addAttribute("conditionEnums", ConditionDTO.list());
		return "pc/activity-add-edit-new";
	}
}