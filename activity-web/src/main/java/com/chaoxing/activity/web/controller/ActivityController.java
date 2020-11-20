package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin
public class ActivityController {

	@Autowired
	private ActivityModuleService service;

	@GetMapping("")
	public String index(Model model) {


		return "pc/index";
	}

	@GetMapping("/getTotal/{current}/{limit}")
	@ResponseBody
	public Long total(@PathVariable Integer current,@PathVariable Integer limit){
		return service.getTotal(current, limit);
	}

}