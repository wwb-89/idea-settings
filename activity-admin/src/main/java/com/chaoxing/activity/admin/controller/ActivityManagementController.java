package com.chaoxing.activity.admin.controller;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**活动管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-12-08 19:06:17
 */
@Slf4j
@Controller
@RequestMapping("activity")
public class ActivityManagementController {

	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityValidationService activityValidationService;

	/**活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param code 图书馆编码
	 * @return java.lang.String
	 */
	@RequestMapping("")
	public String index(String code) {
		return "pc/activity-list";
	}
	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 19:27:03
	 * @param model
	 * @param activityId
	 * @param request
	 * @return java.lang.String
	*/
	@RequestMapping("{activityId}")
	public String index(Model model, @PathVariable Integer activityId, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityValidationService.manageAble(activityId, loginUser, null);
		model.addAttribute("activity", activity);
		SignActivityManageIndexDTO signActivityManageIndex = signApiService.statSignActivityManageIndex(activity.getSignId());
		model.addAttribute("signActivityManageIndex", signActivityManageIndex);
		return "pc/activity-index";
	}

}
