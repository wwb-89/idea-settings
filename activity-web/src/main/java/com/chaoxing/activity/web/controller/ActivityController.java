package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.pageShowModel;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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

	@Autowired
	private ActivityModuleService activityModuleService;

	@Autowired
	private ActivityClassifyQueryService activityClassifyQueryService;

	@Autowired
	private ActivityQueryService queryService;

	@GetMapping("/index")
	public String index(Model model, HttpServletRequest  request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(new ArrayList(){{add(fid);}});
		model.addAttribute("activityClassifyNames",activityClassifyNames);
		model.addAttribute("regions",new ArrayList<>());
		model.addAttribute("fids",new ArrayList<>());
		return "pc/index";
	}





	@ResponseBody
	@GetMapping("/get/{id}")
	public Activity getActivityById( @PathVariable Integer id){
		Activity byId = queryService.getById(id);
		return byId;
	}



}