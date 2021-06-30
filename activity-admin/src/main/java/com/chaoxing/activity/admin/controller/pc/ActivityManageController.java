package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityFlagSignModule;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyHandleService;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
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
public class ActivityManageController {

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
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

	/**活动管理主页
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
		model.addAttribute("code", code);
		model.addAttribute("fid", fid);
		model.addAttribute("secondClassroomFlag", secondClassroomFlag);
		model.addAttribute("strict", strict);
		flag = calActivityFlag(flag, secondClassroomFlag);
		model.addAttribute("activityFlag", flag);
		return "pc/activity-list";
	}

	/**计算活动标示
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
		}
		Activity.ActivityFlagEnum activityFlag = Activity.ActivityFlagEnum.fromValue(flag);
		if (activityFlag == null) {
			flag = Activity.ActivityFlagEnum.NORMAL.getValue();
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
		String areaCode = "";
		if (StringUtils.isNotBlank(code)) {
			// 根据code查询areaCode
			Group group = groupService.getByCode(code);
			if (group != null) {
				areaCode = group.getAreaCode();
			}
		}
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		// 活动类型列表
		List<ActivityTypeDTO> activityTypes = activityQueryService.listActivityType();
		model.addAttribute("activityTypes", activityTypes);
		// 活动分类列表范围
		activityClassifyHandleService.cloneSystemClassify(loginUser.getFid());
		ActivityCreatePermissionDTO activityCreatePermission = activityCreatePermissionService.getGroupClassifyByUserPermission(loginUser.getFid(), loginUser.getUid());
		model.addAttribute("activityClassifies", activityCreatePermission.getActivityClassifies());
		model.addAttribute("existNoLimitPermission", activityCreatePermission.getExistNoLimitPermission());
		model.addAttribute("groupType", activityCreatePermission.getGroupType());
		// 报名签到
		model.addAttribute("sign", SignAddEditDTO.builder().build());
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(loginUser.getFid(), flag);
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
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(loginUser.getFid());
		List<WfwRegionalArchitectureDTO> participatedOrgs = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			participatedOrgs = wfwRegionalArchitectures.stream().filter(v -> Objects.equals(v.getFid(), loginUser.getFid())).collect(Collectors.toList());
		}
		model.addAttribute("participatedOrgs", participatedOrgs);
		return "pc/activity-add-edit";
	}
}