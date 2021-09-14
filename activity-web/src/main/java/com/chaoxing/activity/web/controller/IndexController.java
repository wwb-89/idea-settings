package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.MyActivityParamDTO;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.exception.LoginRequiredException;
import com.chaoxing.activity.web.util.LoginUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
	@Resource
	private UcApiService ucApiService;
	@Resource
	private MarketQueryService marketQueryService;

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
	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer banner, String style, @RequestParam(defaultValue = "") String flag, Integer marketId) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, null, realFid, null, banner, style, flag, marketId);
	}

	/**鄂尔多斯活动广场
	* @Description 
	* @author huxiaolong
	* @Date 2021-09-03 15:41:33
	* @param request
	* @param model
	* @param wfwfid
	* @param unitId
	* @param state
	* @param fid
	* @param flag
	* @param marketId
	* @return java.lang.String
	*/
	@RequestMapping("erdos")
	public String erdosIndex(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid,
							 @RequestParam(defaultValue = "") String flag, Integer marketId, @RequestParam(defaultValue = "true") Boolean levelFilter) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		levelFilter = Optional.ofNullable(levelFilter).orElse(true);
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		return erdos(request, model, realFid, loginUser == null ? null : loginUser.getUid(), flag, marketId, levelFilter);
	}

	private String erdos(HttpServletRequest request, Model model, Integer fid, Integer uid, String flag, Integer marketId, Boolean levelFilter) {
		List<Classify> classifies;
		if (marketId == null) {
			if (fid == null) {
				LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
				Optional.ofNullable(loginUser).orElseThrow(() -> new LoginRequiredException());
				fid = loginUser.getFid();
			}
			classifies = classifyQueryService.listOrgClassifies(fid);
		} else {
			classifies = classifyQueryService.listMarketClassifies(marketId);
		}
		List<String> classifyNames = Optional.ofNullable(classifies).orElse(Lists.newArrayList()).stream().map(Classify::getName).collect(Collectors.toList());
		model.addAttribute("classifyNames", classifyNames);
		model.addAttribute("topFid", fid);
		model.addAttribute("marketId", marketId);
		model.addAttribute("flag", flag);
		model.addAttribute("levelFilter", levelFilter);
		// 获取用户班级
		if (uid != null) {
			Integer userClassId = Optional.ofNullable(ucApiService.getUserExtraInfoByFidAndUid(fid, uid)).map(UserExtraInfoDTO::getClassId).orElse(null);
			model.addAttribute("userClassId", userClassId);
		}
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/special/erdos-index";
		} else {
			return "pc/special/erdos-index";
		}

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
	@RequestMapping("lib")
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
	@RequestMapping("bas")
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
	@RequestMapping("edu")
	public String eduIndex(HttpServletRequest request, Model model, String code, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer pageId, Integer banner, String style, @RequestParam(defaultValue = "") String flag, Integer marketId) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		style = Optional.ofNullable(style).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE);
		return handleData(request, model, code, realFid, pageId, banner, style, flag, marketId);
	}

	private String handleData(HttpServletRequest request, Model model, String code, Integer fid, Integer pageId, Integer banner, String style, String flag, Integer marketId) {
		// 根据fid和flag查询模版
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			marketId = marketQueryService.getMarketIdByTemplate(fid, flag);
		}
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
//	 * @param areaCode
//	 * @param flag 活动标示：双选会、第二课堂等
	 * @return java.lang.String
	 */
	@LoginRequired
	@RequestMapping("my")
	public String my(HttpServletRequest request, Model model, MyActivityParamDTO myActivityParam) throws UnsupportedEncodingException {
		model.addAttribute("areaCode", myActivityParam.getAreaCode());
		model.addAttribute("flag", myActivityParam.getFlag());
		model.addAttribute("hide", myActivityParam.getHide());
		model.addAttribute("title", StringUtils.isBlank(myActivityParam.getTitle()) ? "我的活动" : myActivityParam.getTitle());
		model.addAttribute("managAble", myActivityParam.getManagAble());
		String backUrl = URLEncoder.encode(myActivityParam.buildBackUrl("http://hd.chaoxing.com/my"), StandardCharsets.UTF_8.name());
		myActivityParam.setWfwFormUrl(myActivityParam.getWfwFormUrl() + "&backurl=" + backUrl);
		model.addAttribute("wfwFormUrl", myActivityParam.getWfwFormUrl());
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/my";
		}
		return "pc/activity/my";
	}

}