package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@Autowired
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private GroupService groupService;
	@Resource
	private ActivityQueryDateService activityQueryDateService;

	@GetMapping("")
	public String index(Model model, HttpServletRequest  request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(new ArrayList() {{
			add(fid);
		}});
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listAll();
		model.addAttribute("activityQueryDates", activityQueryDates);
		model.addAttribute("regions", new ArrayList<>());
		model.addAttribute("areaCode", "");
		model.addAttribute("topFid", fid);
		return "pc/index";
	}


	@GetMapping("group/{groupCode}/{fid}")
	public String index(Model model, @PathVariable String groupCode, @PathVariable Integer fid) {
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(new ArrayList() {{
			add(fid);
		}});
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		// 查询地区列表
		List<GroupRegionFilter> groupRegionFilters = groupRegionFilterService.listByGroupCode(groupCode);
		model.addAttribute("regions", groupRegionFilters);
		Group group = groupService.getByCode(groupCode);
		String areaCode = "";
		if (group != null) {
			areaCode = group.getAreaCode();
		}
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listAll();
		model.addAttribute("activityQueryDates", activityQueryDates);
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("topFid", fid);
		return "pc/index";
	}

}