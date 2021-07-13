package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyHandleService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwAreaApiService;
import com.chaoxing.activity.service.org.OrgService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
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
public class ActivityManagementController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private GroupService groupService;
	@Resource
	private ActivityClassifyHandleService activityClassifyHandleService;
	@Resource
	private ActivityCreatePermissionService activityCreatePermissionService;
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

	/**新活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 15:32:59
	 * @param model
	 * @param code 图书馆专用的code
	 * @param fid 空间或微服务后台进入时查询的活动以该fid为主
	 * @param secondClassroomFlag 第二课堂标识
	 * @param strict 是不是严格模式， 严格模式：只显示自己创建的活动
	 * @param flag 活动标示。通用、第二课堂、双选会...
	 * @return java.lang.String
	*/
	public String index(Model model, String code, Integer fid, Integer secondClassroomFlag, Integer strict, String flag) {
		code = Optional.ofNullable(code).orElse("");
		// 防止挂接到三放也携带了code参数
		code = code.split(CommonConstant.DEFAULT_SEPARATOR)[0];
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		List<MarketTableField> marketTableFields = tableFieldQueryService.listMarketTableField(fid, flag, TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		Integer tableFieldId = Optional.ofNullable(tableFieldDetails).orElse(Lists.newArrayList()).stream().findFirst().map(TableFieldDetail::getTableFieldId).orElse(null);
		model.addAttribute("tableFieldId", tableFieldId);
		model.addAttribute("tableFieldDetails", tableFieldDetails);
		model.addAttribute("marketTableFields", marketTableFields);

		model.addAttribute("code", code);
		model.addAttribute("fid", fid);
		model.addAttribute("secondClassroomFlag", secondClassroomFlag);
		model.addAttribute("strict", strict);
		flag = calActivityFlag(flag, secondClassroomFlag);
		model.addAttribute("activityFlag", flag);
		return "pc/activity-list";
	}

	/**计算活动标识
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 14:50:27
	 * @param flag
	 * @param secondClassroomFlag
	 * @return java.lang.String
	*/
	private String calActivityFlag(String flag, Integer secondClassroomFlag) {
		if (StringUtils.isBlank(flag)) {
			if (Objects.equals(secondClassroomFlag, 1)) {
				flag = Activity.ActivityFlagEnum.SECOND_CLASSROOM.getValue();
			} else {
				flag = Activity.ActivityFlagEnum.NORMAL.getValue();
			}
		} else {
			Activity.ActivityFlagEnum activityFlag = Activity.ActivityFlagEnum.fromValue(flag);
			flag = Optional.ofNullable(activityFlag).map(Activity.ActivityFlagEnum::getValue).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
		}
		return flag;
	}

	/**新增活动页面
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 14:49:00
	 * @param model
	 * @param request
	 * @param code
	 * @param secondClassroomFlag
	 * @param flag
	 * @return java.lang.String
	*/
	public String add(Model model, HttpServletRequest request, String code, Integer secondClassroomFlag, String flag, Integer strict) {
		flag = calActivityFlag(flag, secondClassroomFlag);
		String areaCode = Optional.ofNullable(code).filter(StringUtils::isNotBlank).map(groupService::getByCode).map(Group::getAreaCode).orElse("");
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		// 活动类型列表
		List<ActivityTypeDTO> activityTypes = activityQueryService.listActivityType();
		model.addAttribute("activityTypes", activityTypes);
		// 活动分类列表范围
		activityClassifyHandleService.cloneSystemClassify(fid);
		ActivityCreatePermissionDTO activityCreatePermission = activityCreatePermissionService.getGroupClassifyByUserPermission(fid, loginUser.getUid());
		model.addAttribute("activityClassifies", activityCreatePermission.getActivityClassifies());
		model.addAttribute("existNoLimitPermission", activityCreatePermission.getExistNoLimitPermission());
		model.addAttribute("groupType", activityCreatePermission.getGroupType());
		// 报名签到
		model.addAttribute("sign", SignCreateParamDTO.builder().build());
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(fid, flag);
		model.addAttribute("webTemplates", webTemplates);
		model.addAttribute("areaCode", areaCode);
		// 微服务组织架构
		model.addAttribute("wfwGroups", activityCreatePermission.getWfwGroups());
		model.addAttribute("activityFlag", flag);
		// flag配置的报名签到的模块
		List<ActivityFlagSignModule> activityFlagSignModules = activityQueryService.listSignModuleByFlag(flag);
		model.addAttribute("activityFlagSignModules", activityFlagSignModules);
		model.addAttribute("strict", strict);
		// 发布范围默认选中当前机构
		List<WfwAreaDTO> wfwRegionalArchitectures = wfwAreaApiService.listByFid(fid);
		List<WfwAreaDTO> participatedOrgs = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			participatedOrgs = wfwRegionalArchitectures.stream().filter(v -> Objects.equals(v.getFid(), fid)).collect(Collectors.toList());
		}
		model.addAttribute("participatedOrgs", participatedOrgs);
		// 是不是定制机构
		boolean customOrg = orgService.isCustomOrg(fid);
		model.addAttribute("customOrg", customOrg);
		return "pc/activity-add-edit";
	}

	public String add(HttpServletRequest request, Model model, Integer templateId, String code) {
		String areaCode = Optional.ofNullable(code).filter(StringUtils::isNotBlank).map(groupService::getByCode).map(Group::getAreaCode).orElse("");
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		// 加载模版对应的组件列表
		List<com.chaoxing.activity.model.Component> components = activityEngineQueryService.listComponentByTemplateId(templateId);
		model.addAttribute("components", components);
		// 构建默认的报名签到创建对象
		SignCreateParamDTO signCreateParamDto = SignCreateParamDTO.buildDefault();
		model.addAttribute("sign", signCreateParamDto);
		// 活动形式列表
		List<ActivityTypeDTO> activityTypes = activityQueryService.listActivityType();
		model.addAttribute("activityTypes", activityTypes);
		// 活动分类列表范围
		activityClassifyHandleService.cloneSystemClassify(fid);
		ActivityCreatePermissionDTO activityCreatePermission = activityCreatePermissionService.getGroupClassifyByUserPermission(fid, loginUser.getUid());
		model.addAttribute("activityClassifies", activityCreatePermission.getActivityClassifies());
		model.addAttribute("existNoLimitPermission", activityCreatePermission.getExistNoLimitPermission());
		model.addAttribute("groupType", activityCreatePermission.getGroupType());
		// 报名签到
		model.addAttribute("sign", SignCreateParamDTO.builder().build());
		String flag = templateQueryService.getActivityFlagByTemplateId(templateId);
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(fid, flag);
		model.addAttribute("webTemplates", webTemplates);
		model.addAttribute("areaCode", areaCode);
		// 微服务组织架构
		model.addAttribute("wfwGroups", activityCreatePermission.getWfwGroups());
		model.addAttribute("activityFlag", flag);
		// 发布范围默认选中当前机构
		List<WfwAreaDTO> wfwAreaDtos = wfwAreaApiService.listByFid(fid);
		List<WfwAreaDTO> participatedOrgs = Optional.ofNullable(wfwAreaDtos).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).collect(Collectors.toList());
		model.addAttribute("participatedOrgs", participatedOrgs);
		// 是不是定制机构
		boolean isCustomOrg = orgService.isCustomOrg(fid);
		model.addAttribute("isCustomOrg", isCustomOrg);
		return "pc/activity-add-edit-new";
	}

}