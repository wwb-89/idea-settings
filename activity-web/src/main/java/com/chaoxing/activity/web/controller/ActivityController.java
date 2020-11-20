package com.chaoxing.activity.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**活动
 * @author wwb
 * @version ver 1.0
 * @className ActivityController
 * @description
 * @blame wwb
 * @date 2020-11-20 10:49:54
 */
@Controller
@RequestMapping("activity")
public class ActivityController {

	@GetMapping("")
	public String index(Model model) {

		return "pc/index";
	}

}