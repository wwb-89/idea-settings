package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**通用活动管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-12-08 19:06:17
 */
@Slf4j
@Controller
@RequestMapping({"general", ""})
public class GeneralActivityController {

	@Resource
	private ActivityController activityController;
	@Resource
	private MarketHandleService marketHandleService;

	/**活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param model
	 * @param marketId
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param strict 是否是严格模式，严格模式下只查询当前用户创建的活动
	 * @param flag
	 * @param pageMode 0：通用的管理列表页面，1：简单的管理列表页面
	 * @param direct true：直接到管理列表页面，false：可以重定向到市场管理页面
	 * @return java.lang.String
	 */
	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, Integer marketId, Integer wfwfid, Integer unitId, Integer state, Integer fid,
						@RequestParam(defaultValue = "0") Integer strict, String flag, @RequestParam(defaultValue = "0") Integer pageMode, @RequestParam(defaultValue = "false") Boolean direct) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid()))));
		direct = Optional.ofNullable(direct).orElse(false);
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
			if (activityFlagEnum == null) {
				throw new BusinessException("未知的flag");
			}
			Template template = marketHandleService.getOrCreateOrgMarket(realFid, activityFlagEnum, LoginUtils.getLoginUser(request));
			marketId = template.getMarketId();
			if (marketId != null && !direct) {
				return "redirect:/market/" + marketId + "?pageMode=" + pageMode;
			}
		}
		return activityController.index(model, marketId, realFid, strict, flag, pageMode);
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-25 10:19:16
	 * @param request
	 * @param model
	 * @param marketId
	 * @param code
	 * @return java.lang.String
	*/
	@GetMapping("activity/add")
	public String add(HttpServletRequest request, Model model, Integer marketId, String flag, String code, @RequestParam(defaultValue = "0") Integer strict) {
		return activityController.add(request, model, marketId, flag, strict);
	}

}