package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.org.OrgService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
	private GroupService groupService;
	@Resource
	private WfwGroupApiService wfwGroupApiService;
	@Resource
	private WfwContactApiService wfwContactApiService;
	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private OrgService orgService;
	@Resource
	private TableFieldQueryService tableFieldQueryService;
	@Resource
	private ActivityEngineQueryService activityEngineQueryService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private ClassifyHandleService classifyHandleService;
	@Resource
	private ClassifyQueryService classifyQueryService;

	/**新活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 15:32:59
	 * @param model
	 * @param marketId
	 * @param code 图书馆专用的code
	 * @param fid 空间或微服务后台进入时查询的活动以该fid为主
	 * @param strict 是不是严格模式， 严格模式：只显示自己创建的活动
	 * @param flag
	 * @return java.lang.String
	*/
	public String index(Model model, Integer marketId, String code, Integer fid, Integer strict, String flag) {
		code = Optional.ofNullable(code).orElse("");
		// 防止挂接到三放也携带了code参数
		code = code.split(CommonConstant.DEFAULT_SEPARATOR)[0];
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		List<MarketTableField> marketTableFields = tableFieldQueryService.listMarketTableField(marketId, TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		Integer tableFieldId = Optional.ofNullable(tableFieldDetails).orElse(Lists.newArrayList()).stream().findFirst().map(TableFieldDetail::getTableFieldId).orElse(null);
		model.addAttribute("tableFieldId", tableFieldId);
		model.addAttribute("tableFieldDetails", tableFieldDetails);
		model.addAttribute("marketTableFields", marketTableFields);

		model.addAttribute("code", code);
		model.addAttribute("fid", fid);
		model.addAttribute("strict", strict);
		model.addAttribute("marketId", marketId);
		model.addAttribute("flag", flag);
		return "pc/activity-list";
	}

	public String add(HttpServletRequest request, Model model, Integer marketId, String flag, String code, Integer strict) {
		flag = Optional.ofNullable(flag).filter(StringUtils::isNotBlank).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		Template template = templateQueryService.getTemplateByMarketIdOrActivityFlag(marketId, Activity.ActivityFlagEnum.fromValue(flag));
		Integer templateId = Optional.ofNullable(template).map(Template::getId).orElse(null);
		model.addAttribute("marketId", marketId);
		model.addAttribute("strict", strict);
		model.addAttribute("templateId", template.getId());
		// 加载模版对应的组件列表
		model.addAttribute("templateComponents", activityEngineQueryService.listTemplateComponentTree(templateId, template.getFid()));
		// 活动形式列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		// 活动分类列表范围
		if (marketId == null) {
			classifyHandleService.cloneSystemClassifyToOrg(fid);
			model.addAttribute("activityClassifies", classifyQueryService.listOrgClassifies(fid));
		} else {
			model.addAttribute("activityClassifies", classifyQueryService.listMarketClassifies(marketId));
		}
		// 报名签到
		model.addAttribute("sign", SignCreateParamDTO.builder().build());
		flag = Optional.of(template).map(Template::getActivityFlag).orElse(flag);
		// 模板列表
		model.addAttribute("webTemplates", webTemplateService.listAvailable(fid, flag));
		model.addAttribute("areaCode", Optional.ofNullable(code).filter(StringUtils::isNotBlank).map(groupService::getByCode).map(Group::getAreaCode).orElse(""));
		// 微服务组织架构
		model.addAttribute("wfwGroups", wfwGroupApiService.buildWfwGroups(wfwGroupApiService.listGroupByFid(fid)));
		// 通讯录组织架构
		model.addAttribute("contactGroups", wfwGroupApiService.buildWfwGroups(wfwContactApiService.listUserContactOrgsByFid(fid)));
		model.addAttribute("activityFlag", flag);
		// 发布范围默认选中当前机构
		List<WfwAreaDTO> wfwAreaDtos = wfwAreaApiService.listByFid(fid);
		List<WfwAreaDTO> participatedOrgs = Optional.ofNullable(wfwAreaDtos).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).collect(Collectors.toList());
		model.addAttribute("participatedOrgs", participatedOrgs);
		// 是不是定制机构
		model.addAttribute("isCustomOrg", orgService.isCustomOrg(fid));
		return "pc/activity-add-edit-new";
	}

}