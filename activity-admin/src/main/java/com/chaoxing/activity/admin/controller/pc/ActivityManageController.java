package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private GroupService groupService;
	@Resource
	private WfwGroupApiService wfwGroupApiService;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 15:32:59
	 * @param model
	 * @param code 图书馆专用的code
	 * @param secondClassroomFlag 第二课堂标识
	 * @param strict 是不是严格模式， 严格模式：只显示自己创建的活动
	 * @return java.lang.String
	*/
	public String index(Model model, String code, Integer secondClassroomFlag, Integer strict) {
		code = Optional.ofNullable(code).orElse("");
		// 防止挂接到三放也携带了code参数
		code = code.split(CommonConstant.DEFAULT_SEPARATOR)[0];
		model.addAttribute("code", code);
		model.addAttribute("secondClassroomFlag", secondClassroomFlag);
		model.addAttribute("strict", strict);
		return "pc/activity-list";
	}

	public String add(Model model, HttpServletRequest request, String code, Integer secondClassroomFlag) {
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
		// 活动分类列表
		model.addAttribute("activityClassifies", activityClassifyQueryService.listOrgOptional(loginUser.getFid()));
		// 报名签到
		model.addAttribute("sign", SignAddEditDTO.builder().build());
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(loginUser.getFid());
		model.addAttribute("webTemplates", webTemplates);
		model.addAttribute("areaCode", areaCode);

		List<WfwGroupDTO> wfwGroups = wfwGroupApiService.getGroupByGid(loginUser.getFid(), "0");
		model.addAttribute("wfwGroups", wfwGroups);
		model.addAttribute("secondClassroomFlag", secondClassroomFlag);
		return "pc/activity-add-edit";
	}

}