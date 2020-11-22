package com.chaoxing.activity.web.controller.mobile;

import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
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
 * @className ActivityController
 * @description
 * @blame wwb
 * @date 2020-11-20 17:04:41
 */
@Controller
@RequestMapping("m/activity")
public class ActivityController {

	@Resource
	private GroupRegionFilterService groupRegionFilterService;
	@Resource
	private ActivityClassifyQueryService activityClassifyQueryService;

	@GetMapping
	public String index(Model model, HttpServletRequest request) {

		return "mobile/index";
	}

	@GetMapping("group/{group}/{fid}")
	public String index(Model model, HttpServletRequest request, @PathVariable String group, @PathVariable Integer fid) {


		List<GroupRegionFilter> groupRegionFilters = groupRegionFilterService.listByGroup(group);
		model.addAttribute("regions", groupRegionFilters);
		return "mobile/index";
	}

}
