package com.chaoxing.activity.admin.controller;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.manager.sign.SignAddEditDTO;
import com.chaoxing.activity.dto.module.SignFormDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.admin.util.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/** 图书馆活动管理
 * @author wwb
 * @version ver 1.0
 * @className LibActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-11-10 14:58:50
 */
@Controller
@RequestMapping({"lib", "bas", "edu"})
public class LibActivityManagementController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private WebTemplateService webTemplateService;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param code 图书馆编码
	 * @return java.lang.String
	*/
	@RequestMapping("")
	public String index(String code) {
		return "pc/activity-index";
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:26:18
	 * @param model
	 * @param request
	 * @return java.lang.String
	*/
	@GetMapping("add")
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

	/**活动修改页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:26:28
	 * @param model
	 * @param activityId
	 * @param request
	 * @param code
	 * @return java.lang.String
	*/
	@GetMapping("{activityId}/edit")
	public String add(Model model, @PathVariable Integer activityId, HttpServletRequest request, String code, Integer step) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityQueryService.getById(activityId);
		model.addAttribute("activity", activity);
		// 活动类型列表
		model.addAttribute("activityTypes", activityQueryService.listActivityType());
		// 活动分类列表
		List<ActivityClassify> activityClassifies = activityClassifyQueryService.listOrgOptional(loginUser.getFid());
		model.addAttribute("activityClassifies", activityClassifies);
		// 报名签到
		Integer signId = activity.getSignId();
		SignFormDTO signForm = null;
		if (signId != null) {
			signForm = signApiService.getById(signId);
		}
		model.addAttribute("sign", signForm);
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.list();
		model.addAttribute("webTemplates", webTemplates);
		model.addAttribute("step", step);
		return "pc/activity-add-edit";
	}

}