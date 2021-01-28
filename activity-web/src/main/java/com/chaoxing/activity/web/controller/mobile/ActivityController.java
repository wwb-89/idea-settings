package com.chaoxing.activity.web.controller.mobile;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
	@Resource
	private GroupService groupService;
	@Resource
	private ActivityQueryDateService activityQueryDateService;

	/**移动端活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 14:32:43
	 * @param model
	 * @param request
	 * @return java.lang.String
	*/
	@GetMapping
	public String index(Model model, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		// 活动分类列表
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(new ArrayList(){{add(fid);}});
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listAll();
		model.addAttribute("activityQueryDates", activityQueryDates);
		// 查询地区列表
		model.addAttribute("regions", new ArrayList<>());
		model.addAttribute("areaCode", "");
		model.addAttribute("topFid", fid);
		return "mobile/index";
	}

	/**移动端组别活动市场
	 * @Description
	 * @author wwb
	 * @Date 2020-11-22 14:33:02
	 * @param model
	 * @param groupCode
	 * @param fid
	 * @return java.lang.String
	*/
	@GetMapping("group/{groupCode}")
	public String index(Model model, @PathVariable String groupCode, @RequestParam("unitId") Integer fid, Integer pageId) {
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(new ArrayList(){{
			add(fid);
		}});
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listAll();
		model.addAttribute("activityQueryDates", activityQueryDates);
		// 查询地区列表
		List<GroupRegionFilter> groupRegionFilters = groupRegionFilterService.listByGroupCode(groupCode);
		model.addAttribute("regions", groupRegionFilters);
		Group group = groupService.getByCode(groupCode);
		String areaCode = "";
		if (group != null) {
			areaCode = group.getAreaCode();
		}
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("topFid", fid);
		model.addAttribute("pageId", pageId);
		return "mobile/index";
	}

	/**我的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-27 17:39:48
	 * @param request
	 * @param model
	 * @return java.lang.String
	*/
	@RequestMapping("my")
	public String my(HttpServletRequest request, Model model) {
		return "mobile/my";
	}

}