package com.chaoxing.activity.admin.controller.mobile;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagerController
 * @description
 * @blame wwb
 * @date 2020-12-24 10:03:49
 */
@Component
public class ActivityManagerController {

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private SignApiService signApiService;

	public String activityIndex(Model model, @PathVariable Integer activityId, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityValidationService.manageAble(activityId, loginUser, null);
		model.addAttribute("activity", activity);
		Integer signId = activity.getSignId();
		SignActivityManageIndexDTO signActivityManageIndex = signApiService.statSignActivityManageIndex(signId);
		model.addAttribute("signActivityManageIndex", signActivityManageIndex);
		return "mobile/activity-index";
	}

}