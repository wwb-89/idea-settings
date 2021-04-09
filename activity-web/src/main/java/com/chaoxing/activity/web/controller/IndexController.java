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

/**活动市场
 * @author wwb
 * @version ver 1.0
 * @className IndexController
 * @description
 * @blame wwb
 * @date 2021-02-01 14:23:07
 */
@Slf4j
@Controller
@RequestMapping("")
public class IndexController {

	/** 默认风格 */
	private static final String DEFAULT_STYLE = "1";

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
	 * @param style 风格
	 * @param flag 活动标示：双选会、第二课堂等
	 * @return java.lang.String
	 */
	@LoginRequired
	@GetMapping("")
	public String index(HttpServletRequest request, Model model, Integer fid, Integer banner, String style, @RequestParam(defaultValue = "") String flag) {
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, null, fid, null, banner, style, flag);
	}

	/**图书馆
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:27
	 * @param request
	 * @param model
	 * @param code
	 * @param unitId 门户封装的fid
	 * @param state 微服务封装的fid
	 * @param pageId
	 * @param fid 其他来源封装的fid
	 * @param banner
	 * @param style
	 * @param flag 活动标示：双选会、第二课堂等
	 * @return java.lang.String
	 */
	@GetMapping("lib")
	public String libIndex(HttpServletRequest request, Model model, String code, Integer unitId, Integer state, Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag) {
		Integer realFid = Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid));
		if (realFid == null) {
			// 使用通用的活动广场
			return "redirect:/?flag=" + flag;
		}
		return handleData(request, model, code, realFid, pageId, banner, style, flag);
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
	 * @param style
	 * @param flag 活动标示：双选会、第二课堂等
	 * @return java.lang.String
	 */
	@LoginRequired
	@GetMapping("bas")
	public String basIndex(HttpServletRequest request, Model model, String code, @RequestParam(value = "unitId", required = false) Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag) {
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, code, fid, pageId, banner, style, flag);
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
	 * @param style
	 * @param flag 活动标示：双选会、第二课堂等
	 * @return java.lang.String
	 */
	@LoginRequired
	@GetMapping("edu")
	public String eduIndex(HttpServletRequest request, Model model, String code, @RequestParam(value = "unitId", required = false) Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag) {
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, code, fid, pageId, banner, style, flag);
	}

	private String handleData(HttpServletRequest request, Model model, String code, Integer fid, Integer pageId, Integer banner, String style, String flag) {
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
		model.addAttribute("flag", flag);
		if (StringUtils.isEmpty(style)) {
			if (UserAgentUtils.isMobileAccess(request)) {
				return "mobile/index-new";
			}
			return "pc/index";
		}else {
			if (UserAgentUtils.isMobileAccess(request)) {
				return "mobile/index-new";
			}
			return "pc/activity/market/activity-market-" + style;
		}
	}

	/**我的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 14:59:23
	 * @param request
	 * @param model
	 * @param areaCode
	 * @param flag 活动标示：双选会、第二课堂等
	 * @return java.lang.String
	 */
	@RequestMapping("my")
	public String my(HttpServletRequest request, Model model, @RequestParam(defaultValue = "") String areaCode, @RequestParam(defaultValue = "") String flag) {
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("flag", flag);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/my";
		}
		return "pc/activity/my";
	}

}