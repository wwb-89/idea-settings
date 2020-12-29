package com.chaoxing.activity.admin.controller.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className GeneralActivityManageController
 * @description
 * @blame wwb
 * @date 2020-12-29 15:19:57
 */
@Controller
@RequestMapping("m/general")
public class GeneralActivityManageController {

	@Resource
	private ActivityManagerController activityManagerController;

	/**活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2020-12-29 15:24:46
	 * @param model
	 * @param activityId
	 * @param request
	 * @return java.lang.String
	 */
	@RequestMapping("activity/{activityId}")
	public String activityIndex(Model model, @PathVariable Integer activityId, HttpServletRequest request) {
		return activityManagerController.activityIndex(model, activityId, request);
	}

}
