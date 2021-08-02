package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.exception.LoginRequiredException;
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
import java.util.stream.Collectors;

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
	private static final String DEFAULT_STYLE = "2";

	@Resource
	private GroupRegionFilterService groupRegionFilterService;
	@Resource
	private ClassifyQueryService classifyQueryService;
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
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param banner
	 * @param style 风格
	 * @param flag 活动标示：双选会、第二课堂等
	 * @param marketId 市场id
	 * @return java.lang.String
	 */
	@GetMapping("")
	public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer banner, String style, @RequestParam(defaultValue = "") String flag, Integer marketId) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, null, realFid, null, banner, style, flag, marketId);
	}

	/**图书馆
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:27
	 * @param request
	 * @param model
	 * @param code
	 * @param wfwfid
	 * @param unitId 门户封装的fid
	 * @param state 微服务封装的fid
	 * @param pageId
	 * @param fid 其他来源封装的fid
	 * @param banner
	 * @param style
	 * @param flag 活动标示：双选会、第二课堂等
	 * @param marketId 市场id
	 * @return java.lang.String
	 */
	@GetMapping("lib")
	public String libIndex(HttpServletRequest request, Model model, String code, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag, Integer marketId) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		return handleData(request, model, code, realFid, pageId, banner, style, flag, marketId);
	}

	/**基础教育
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:36
	 * @param request
	 * @param model
	 * @param code
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param pageId
	 * @param banner
	 * @param style
	 * @param flag 活动标示：双选会、第二课堂等
	 * @param marketId 市场id
	 * @return java.lang.String
	 */
	@GetMapping("bas")
	public String basIndex(HttpServletRequest request, Model model, String code, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag, Integer marketId) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, code, realFid, pageId, banner, style, flag, marketId);
	}

	/**高校
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:44
	 * @param request
	 * @param model
	 * @param code
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param pageId
	 * @param banner
	 * @param style
	 * @param flag 活动标示：双选会、第二课堂等
	 * @param marketId 市场id
	 * @return java.lang.String
	 */
	@GetMapping("edu")
	public String eduIndex(HttpServletRequest request, Model model, String code, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag, Integer marketId) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, code, realFid, pageId, banner, style, flag, marketId);
	}

	private String handleData(HttpServletRequest request, Model model, String code, Integer fid, Integer pageId, Integer banner, String style, String flag, Integer marketId) {
		List<Classify> classifies;
		if (marketId == null) {
			if (fid == null) {
				LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
				Optional.ofNullable(loginUser).orElseThrow(() -> new LoginRequiredException());
				fid = loginUser.getFid();
			}
			classifies = classifyQueryService.listOrgClassifies(fid);
		}else {
			classifies = classifyQueryService.listMarketClassifies(marketId);
		}
		List<String> classifyNames = Optional.ofNullable(classifies).orElse(Lists.newArrayList()).stream().map(Classify::getName).collect(Collectors.toList());
		model.addAttribute("classifyNames", classifyNames);
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
		model.addAttribute("marketId", marketId);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/index";
		}else {
			if (StringUtils.isEmpty(style)) {
				return "pc/index";
			}else {
				return "pc/activity/market/activity-market-" + style;
			}
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
	@LoginRequired
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