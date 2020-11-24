package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.pageShowModel;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

	@Resource
	private GroupRegionFilterService groupRegionFilterService;

	@Resource
	private GroupService groupService;

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

	@GetMapping("group/{groupCode}/{fid}")
	public String index(Model model, @PathVariable String groupCode, @PathVariable Integer fid) {
		// 活动分类列表
		List<Integer> fids = groupService.listGroupFid(groupCode);
		if (!fids.contains(fid)) {
			fids.add(fid);
		}
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(fids);
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		// 查询地区列表
		List<GroupRegionFilter> groupRegionFilters = groupRegionFilterService.listByGroupCode(groupCode);
		model.addAttribute("regions", groupRegionFilters);
		model.addAttribute("fids", fids);
		return "pc/index";
	}






	@ResponseBody
	@GetMapping("/get/{id}")
	public Activity getActivityById( @PathVariable Integer id){
		Activity byId = queryService.getById(id);
		return byId;
	}



}