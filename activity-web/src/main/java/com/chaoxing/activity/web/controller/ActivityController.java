package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.web.util.LoginUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**活动
 * @author wwb
 * @version ver 1.0
 * @className ActivityController
 * @description
 * @blame wwb
 * @date 2020-11-20 10:49:54
 */
@Slf4j
@Controller
@RequestMapping("")
public class ActivityController {

	@Resource
	private GroupRegionFilterService groupRegionFilterService;
	@Resource
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private GroupService groupService;
	@Resource
	private ActivityQueryDateService activityQueryDateService;

	/**通用
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-09 10:22:16
	 * @param request
	 * @param model
	 * @param fid
	 * @param banner
	 * @return java.lang.String
	*/
	@LoginRequired
	@GetMapping("")
	public String index(HttpServletRequest request, Model model, Integer fid, Integer banner) {
		return handleData(request, model, null, fid, null, banner);
	}

	/**图书馆
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-09 10:22:27
	 * @param request
	 * @param model
	 * @param code
	 * @param unitId
	 * @param state
	 * @param pageId
	 * @param fid
	 * @param banner
	 * @return java.lang.String
	*/
	@GetMapping("lib")
	public String libIndex(HttpServletRequest request, Model model, String code, Integer unitId, Integer state, Integer fid, Integer pageId, Integer banner) {
		Integer realFid = Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid));
		if (realFid == null) {
			return "redirect:/";
		}
		return handleData(request, model, code, realFid, pageId, banner);
	}
	
	/**基础教育
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-09 10:22:36
	 * @param request
	 * @param model
	 * @param code
	 * @param fid
	 * @param pageId
	 * @param banner
	 * @return java.lang.String
	*/
	@LoginRequired
	@GetMapping("bas")
	public String basIndex(HttpServletRequest request, Model model, String code, @RequestParam(value = "unitId", required = false) Integer fid, Integer pageId, Integer banner) {
		return handleData(request, model, code, fid, pageId, banner);
	}
	
	/**高校
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-09 10:22:44
	 * @param request
	 * @param model
	 * @param code
	 * @param fid
	 * @param pageId
	 * @param banner
	 * @return java.lang.String
	*/
	@LoginRequired
	@GetMapping("edu")
	public String eduIndex(HttpServletRequest request, Model model, String code, @RequestParam(value = "unitId", required = false) Integer fid, Integer pageId, Integer banner) {
		return handleData(request, model, code, fid, pageId, banner);
	}

	private String handleData(HttpServletRequest request, Model model, String code, Integer fid, Integer pageId, Integer banner) {
		if (fid == null) {
			fid = LoginUtils.getLoginUser(request).getFid();
		}
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(Lists.newArrayList(fid));
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		// 查询地区列表
		List<GroupRegionFilter> groupRegionFilters;
		String areaCode = "";
		if (StringUtils.isNotBlank(code)) {
			groupRegionFilters = groupRegionFilterService.listByGroupCode(code);
			Group group = groupService.getByCode(code);
			if (group != null) {
				areaCode = group.getAreaCode();
			}
		} else {
			groupRegionFilters = Lists.newArrayList();
		}
		model.addAttribute("regions", groupRegionFilters);
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listAll();
		model.addAttribute("activityQueryDates", activityQueryDates);
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("topFid", fid);
		model.addAttribute("pageId", pageId);
		banner = Optional.ofNullable(banner).orElse(0);
		model.addAttribute("banner", banner);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/index";
		}
		return "pc/index";
	}

}