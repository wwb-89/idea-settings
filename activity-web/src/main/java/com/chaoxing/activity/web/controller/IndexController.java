package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivitySquareParamDTO;
import com.chaoxing.activity.dto.activity.MyActivityParamDTO;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.model.MarketSignUpConfig;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.enums.SignUpBtnEnum;
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
import java.util.Arrays;
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
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private MarketSignupConfigService marketSignupConfigService;
	@Resource
	private ActivityQueryService activityQueryService;

	private static final String DEFAULT_HIDE = "signedUp,collected,managing";

	private static final String SIGNED_UP_TAB = "signedUp";

	private static final String MANAGING_TAB = "managing";

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
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listUniversalDateScope();
		return handleData(request, model, null, activitySquareParam, activityQueryDates);
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
		model.addAttribute("hideFilter", activitySquareParam.getHideFilter());
		model.addAttribute("levelFilter", levelFilter);
		model.addAttribute("mainDomain", DomainConstant.MAIN);
		model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
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
	 * @param pageId
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("lib")
	public String libIndex(HttpServletRequest request, Model model, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listZjlibDateScope();
		return handleData(request, model, pageId, activitySquareParam, activityQueryDates);
	}

	/**基础教育
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:36
	 * @param request
	 * @param model
	 * @param pageId
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("bas")
	public String basIndex(HttpServletRequest request, Model model, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		activitySquareParam.setStyle(Optional.ofNullable(activitySquareParam.getStyle()).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE));
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listUniversalDateScope();
		return handleData(request, model, pageId, activitySquareParam, activityQueryDates);
	}

	/**高校
	 * @Description
	 * @author wwb
	 * @Date 2020-12-09 10:22:44
	 * @param request
	 * @param model
	 * @param pageId
	 * @param activitySquareParam 活动广场参数
	 * @return java.lang.String
	 */
	@RequestMapping("edu")
	public String eduIndex(HttpServletRequest request, Model model, Integer pageId, ActivitySquareParamDTO activitySquareParam) {
		activitySquareParam.setStyle(Optional.ofNullable(activitySquareParam.getStyle()).filter(StringUtils::isNotBlank).orElse(DEFAULT_STYLE));
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listUniversalDateScope();
		return handleData(request, model, pageId, activitySquareParam, activityQueryDates);
	}

	private String handleData(HttpServletRequest request, Model model, Integer pageId, ActivitySquareParamDTO activitySquareParam, List<ActivityQueryDateDTO> activityQueryDates) {
		// 参数中传递的fid
		Integer fid = activitySquareParam.getRealFid();
		Integer marketId = activitySquareParam.getMarketId();
		String flag = activitySquareParam.getFlag();
		String areaCode = activitySquareParam.getAreaCode();
		if (marketId == null && fid == null) {
			LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
			Optional.ofNullable(loginUser).orElseThrow(() -> new LoginRequiredException());
			fid = loginUser.getFid();
		}
		// 根据fid和flag查询模版
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			if (StringUtils.isNotBlank(areaCode)) {
				// 查询code对应的机构fid
				WfwAreaDTO topWfwArea = wfwAreaApiService.getTopWfwArea(areaCode);
				fid = Optional.ofNullable(topWfwArea).map(WfwAreaDTO::getFid).orElse(fid);
			}
			marketId = marketQueryService.getMarketIdByFlag(fid, flag);

		}
		List<Classify> classifies;
		Integer classifyFid = fid;
		if (marketId == null) {
			if (StringUtils.isNotBlank(areaCode)) {
				// 查询区域所属的机构
				classifyFid = groupService.getGroupFid(areaCode);
			}
			classifyFid = Optional.ofNullable(classifyFid).orElse(fid);
			classifies = classifyQueryService.listOrgClassifies(classifyFid);
		} else {
			classifies = classifyQueryService.listMarketClassifies(marketId);
		}
		List<String> classifyNames = Optional.ofNullable(classifies).orElse(Lists.newArrayList()).stream().map(Classify::getName).collect(Collectors.toList());
		model.addAttribute("classifyNames", classifyNames);
		// 查询地区列表
		List<GroupRegionFilter> groupRegionFilters;
		if (StringUtils.isNotBlank(areaCode)) {
			groupRegionFilters = groupRegionFilterService.listByGroupCode(areaCode);
		} else {
			groupRegionFilters = Lists.newArrayList();
		}
		String signUpKeyword = SignUpBtnEnum.BTN_1.getKeyword();
		if (StringUtils.isBlank(areaCode) && marketId != null) {
			MarketSignUpConfig marketSignUpConfig = marketSignupConfigService.get(marketId);
			if (marketSignUpConfig != null) {
				signUpKeyword = StringUtils.isNotBlank(marketSignUpConfig.getSignUpKeyword()) ? marketSignUpConfig.getSignUpKeyword() : signUpKeyword;
			}
		}
		model.addAttribute("regions", groupRegionFilters);
		model.addAttribute("activityQueryDates", activityQueryDates);
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("topFid", fid);
		model.addAttribute("pageId", pageId);
		Integer banner = Optional.ofNullable(activitySquareParam.getBanner()).orElse(0);
		model.addAttribute("banner", banner);
		model.addAttribute("flag", flag);
		// code不为空应该查询区域活动（不能查询市场下的活动）
		model.addAttribute("marketId", StringUtils.isNotBlank(areaCode) ? "" : marketId);
		model.addAttribute("scope", activitySquareParam.getScope());
		model.addAttribute("hideFilter", activitySquareParam.getHideFilter());
		model.addAttribute("signUpAble", Objects.equals(1, activitySquareParam.getStrict()));
		model.addAttribute("signUpKeyword", signUpKeyword);
		model.addAttribute("sw", activitySquareParam.getSw());
		model.addAttribute("mainDomain", DomainConstant.MAIN);
		model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
		// 验证style是否存在
		String style = activitySquareParam.getStyle();
		if (UserAgentUtils.isMobileAccess(request)) {
			if (StringUtils.isEmpty(style)) {
				style = "1";
			}
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
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer uid = loginUser.getUid();
		Integer fid = myActivityParam.getFid();
		String flag = myActivityParam.getFlag();

		String hide = Optional.ofNullable(myActivityParam.getHide()).orElse(DEFAULT_HIDE);
		List<String> hideList = Arrays.asList(hide.split(CommonConstant.DEFAULT_SEPARATOR));
		int countWaitAudit = 0;
		int countManageActivity = 0;
		if (hideList.size() <= 1 && !hideList.contains(SIGNED_UP_TAB)) {
			// 隐藏的tab只有最多只有一个，且其中隐藏的不包含signedUp，查询我报名的活动中是否有待审批，若无，则tab名称更名为已报名，若有，则保持原有的报名
			countWaitAudit = activityQueryService.countApprovalSignedUpActivity(uid, fid, flag);
		}
		if (!hideList.contains(MANAGING_TAB)) {
			// 隐藏的tab不包含管理，则查询一下当前用户是否存在管理的活动
			countManageActivity = activityQueryService.countUserManaged(uid, fid, flag);
		}
		model.addAttribute("hasWaitApprovalSignUp", countWaitAudit > 0);
		model.addAttribute("hasManageActivity", countManageActivity > 0);
		model.addAttribute("fid", fid);
		model.addAttribute("flag", flag);
		model.addAttribute("hide", hide);
		model.addAttribute("title", StringUtils.isBlank(myActivityParam.getTitle()) ? "我的活动" : myActivityParam.getTitle());
		if (StringUtils.isNotBlank(myActivityParam.getAddUrl())) {
			model.addAttribute("addUrl", myActivityParam.getAddUrl());
		}
		if (StringUtils.isNotBlank(myActivityParam.getAddBtnName())) {
			model.addAttribute("addBtnName", myActivityParam.getAddBtnName());
		}
		model.addAttribute("mainDomain", DomainConstant.MAIN);
		model.addAttribute("adminDomain", DomainConstant.ADMIN);
		model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/my";
		}
		return "pc/activity/my";
	}

}