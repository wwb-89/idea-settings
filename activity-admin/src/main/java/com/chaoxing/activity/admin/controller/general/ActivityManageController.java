package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private ActivityScopeQueryService activityScopeQueryService;
	@Resource
	private ActivityValidationService activityValidationService;

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
		Activity activity = activityValidationService.manageAble(activityId, loginUser, null);
		model.addAttribute("activity", activity);
		Integer signId = activity.getSignId();
		SignActivityManageIndexDTO signActivityManageIndex = signApiService.statSignActivityManageIndex(signId);
		model.addAttribute("signActivityManageIndex", signActivityManageIndex);
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
	 * @param code
	 * @return java.lang.String
	 */
	@GetMapping("{activityId}/edit")
	public String edit(Model model, @PathVariable Integer activityId, HttpServletRequest request, String code, Integer step) {
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
		SignAddEditDTO sign = SignAddEditDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getById(signId);
		}
		model.addAttribute("sign", sign);
		// 模板列表
		List<WebTemplate> webTemplates = webTemplateService.listAvailable(loginUser.getFid());
		model.addAttribute("webTemplates", webTemplates);
		model.addAttribute("step", step);
		// 活动参与范围
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = activityScopeQueryService.listByActivityId(activityId);
		model.addAttribute("participatedOrgs", wfwRegionalArchitectures);
		return "pc/activity-add-edit";
	}

}