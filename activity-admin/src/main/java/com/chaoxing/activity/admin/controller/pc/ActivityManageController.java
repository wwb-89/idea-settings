package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

	public String index(String code) {
		return "pc/activity-list";
	}

	public String add(Model model, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		// 活动类型列表
		List<ActivityTypeDTO> activityTypes = activityQueryService.listActivityType();
		model.addAttribute("activityTypes", activityTypes);
		// 活动分类列表
		model.addAttribute("activityClassifies", activityClassifyQueryService.listOrgOptional(loginUser.getFid()));
		// 报名签到
		model.addAttribute("sign", SignAddEditDTO.builder().build());
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.list();
		model.addAttribute("webTemplates", webTemplates);
		return "pc/activity-add-edit";
	}

}
