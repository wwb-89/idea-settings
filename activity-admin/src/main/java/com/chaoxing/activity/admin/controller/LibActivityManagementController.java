package com.chaoxing.activity.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	private ActivityManageController activityManageController;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param code 图书馆编码
	 * @return java.lang.String
	*/
	@RequestMapping("")
	public String index(String code) {
		return activityManageController.index(code);
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:26:18
	 * @param model
	 * @param request
	 * @return java.lang.String
	*/
	@GetMapping("activity/add")
	public String add(Model model, HttpServletRequest request) {
		return activityManageController.add(model, request);
	}

	/**活动主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-25 10:18:28
	 * @param model
	 * @param activityId
	 * @param request
	 * @return java.lang.String
	*/
	@RequestMapping("activity/{activityId}")
	public String activityIndex(Model model, @PathVariable Integer activityId, HttpServletRequest request) {
		return activityManageController.activityIndex(model, activityId, request);
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
	@GetMapping("activity/{activityId}/edit")
	public String edit(Model model, @PathVariable Integer activityId, HttpServletRequest request, String code, Integer step) {
		return activityManageController.edit(model, activityId, request, code, step);
	}

}