package com.chaoxing.activity.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/** 活动管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-11-10 14:58:50
 */
@Controller
@RequestMapping("manage/activity")
public class ActivityManagementController {

	public String index() {
		return "";
	}

	@GetMapping("add")
	public String add(Model model) {
		return "pc/management/activity-add-edit";
	}

	@GetMapping("{activityId}/edit")
	public String add(Model model, @PathVariable Integer activityId) {
		return "pc/management/activity-add-edit";
	}

}