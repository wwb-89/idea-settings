package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivitySquareParamDTO;
import com.chaoxing.activity.dto.activity.MyActivityParamDTO;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityFlagCodeService;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.DomainConstant;
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
import java.util.Objects;
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
	private static final List<String> STYLES = Lists.newArrayList("1", "2");

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
	@Resource
	private ActivityFlagCodeService activityFlagCodeService;

	/**通用
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:16
	 * @param request
	 * @param model
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, ActivitySquareParamDTO activitySquareParam) {
		activitySquareParam.setStyle(Optional.ofNullable(activitySquareParam.getStyle()).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE));
		return handleData(request, model, null, null, activitySquareParam);
	}

	/**鄂尔多斯活动广场
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-03 15:41:33
	 * @param request
	 * @param model
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("erdos")
	public String erdosIndex(HttpServletRequest request, Model model, @RequestParam(defaultValue = "true") Boolean levelFilter, ActivitySquareParamDTO activitySquareParam) {
		levelFilter = Optional.ofNullable(levelFilter).orElse(true);
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		return erdos(request, model, loginUser == null ? null : loginUser.getUid(), levelFilter, activitySquareParam);
	}

	private String erdos(HttpServletRequest request, Model model, Integer uid, Boolean levelFilter, ActivitySquareParamDTO activitySquareParam) {
		List<Classify> classifies;
		Integer fid = activitySquareParam.getRealFid();
		Integer marketId = activitySquareParam.getMarketId();
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
		model.addAttribute("flag", activitySquareParam.getFlag());
		model.addAttribute("levelFilter", levelFilter);
		model.addAttribute("timeOrder", activitySquareParam.getTimeOrder());
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
	 * @param pageId
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("lib")
	public String libIndex(HttpServletRequest request, Model model, String code, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		if (StringUtils.isBlank(code)) {
			// todo 后续移除code参数，改用flag获取
			code = activityFlagCodeService.getCodeByFlag(activitySquareParam.getFlag());
		}
		return handleData(request, model, code, pageId, activitySquareParam);
	}

	/**基础教育
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:36
	 * @param request
	 * @param model
	 * @param code
	 * @param pageId
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("bas")
	public String basIndex(HttpServletRequest request, Model model, String code, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		activitySquareParam.setStyle(Optional.ofNullable(activitySquareParam.getStyle()).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE));
		if (StringUtils.isBlank(code)) {
			//	todo 后续移除code参数，改用flag获取
			code = activityFlagCodeService.getCodeByFlag(activitySquareParam.getFlag());
		}
		return handleData(request, model, code, pageId, activitySquareParam);
	}

	/**高校
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:44
	 * @param request
	 * @param model
	 * @param code
	 * @param pageId
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("edu")
	public String eduIndex(HttpServletRequest request, Model model, String code, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		activitySquareParam.setStyle(Optional.ofNullable(activitySquareParam.getStyle()).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE));
		if (StringUtils.isBlank(code)) {
			//	todo 后续移除code参数，改用flag获取
			code = activityFlagCodeService.getCodeByFlag(activitySquareParam.getFlag());
		}
		return handleData(request, model, code, pageId, activitySquareParam);
	}

	private String handleData(HttpServletRequest request, Model model, String code, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		Integer fid = activitySquareParam.getRealFid();
		Integer marketId = activitySquareParam.getMarketId();
		String flag = activitySquareParam.getFlag();
		// 根据fid和flag查询模版
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			marketId = marketQueryService.getMarketIdByFlag(fid, flag);
		}
		List<Classify> classifies;
		Integer classifyFid = fid;
		if (marketId == null) {
			if (StringUtils.isNotBlank(code)) {
				// 查询区域所属的机构
				classifyFid = groupService.getGroupFid(code);
			}
			if (fid == null) {
				LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
				Optional.ofNullable(loginUser).orElseThrow(() -> new LoginRequiredException());
				fid = loginUser.getFid();
			}
			classifyFid = Optional.ofNullable(classifyFid).orElse(fid);
			classifies = classifyQueryService.listOrgClassifies(classifyFid);
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
		Integer banner = Optional.ofNullable(activitySquareParam.getBanner()).orElse(0);
		model.addAttribute("banner", banner);
		model.addAttribute("flag", flag);
		model.addAttribute("marketId", marketId);
		model.addAttribute("scope", activitySquareParam.getScope());
		model.addAttribute("hideFilter", activitySquareParam.getHideFilter());
		model.addAttribute("signUpAble", Objects.equals(1, activitySquareParam.getStrict()));
		model.addAttribute("timeOrder", activitySquareParam.getTimeOrder());
		// 验证style是否存在
		String style = activitySquareParam.getStyle();
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/index-" + style;
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
	 * @param myActivityParam
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
		String backUrl = URLEncoder.encode(myActivityParam.buildBackUrl(DomainConstant.WEB_DOMAIN + "/my"), StandardCharsets.UTF_8.name());
		myActivityParam.setWfwFormUrl(myActivityParam.getWfwFormUrl() + "&backurl=" + backUrl);
		model.addAttribute("wfwFormUrl", myActivityParam.getWfwFormUrl());
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/my";
		}
		return "pc/activity/my";
	}

}