package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.market.MarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.MarketUpdateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**活动市场
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketController
 * @description
 * @blame wwb
 * @date 2021-07-16 15:25:17
 */
@Controller
@RequestMapping("market")
public class MarketController {

	@Resource
	private MarketQueryService marketQueryService;
	@Resource
	private MarketValidationService marketValidationService;

	/**活动市场管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-18 20:41:17
	 * @param request
	 * @param model
	 * @param areaCode
	 * @param pageMode
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("{marketId}")
	public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId, String areaCode, @RequestParam(defaultValue = "0") Integer pageMode) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Market market = marketValidationService.manageAble(marketId, loginUser.buildOperateUserDTO());
		model.addAttribute("market", market);
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("pageMode", pageMode);
		return "pc/market/market-index-new";
	}

	/**微服务创建营应用页面
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-18 20:11:11
	 * @param request
	 * @param model
	 * @param classifyId
	 * @param fid
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("new/from-wfw")
	public String newFromWfw(HttpServletRequest request, Model model, Integer classifyId, Integer fid, String activityFlag, String backUrl) {
		fid = Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid());
		activityFlag = Optional.ofNullable(activityFlag).filter(StringUtils::isNotBlank).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
		MarketCreateParamDTO market = MarketCreateParamDTO.build(fid, classifyId, activityFlag);
		model.addAttribute("market", market);
		model.addAttribute("backUrl", backUrl);
		model.addAttribute("activityFlag", activityFlag);
		return "pc/market/wfw-market";
	}

	@LoginRequired
	@RequestMapping("update/from-wfw")
	public String updateFromWfw(HttpServletRequest request, Model model, Integer classifyId, Integer fid, Integer appId, String backUrl) {
		MarketUpdateParamDTO market = marketQueryService.getByWfwAppId(appId);
		model.addAttribute("market", market);
		model.addAttribute("backUrl", backUrl);
		return "pc/market/wfw-market";
	}

}