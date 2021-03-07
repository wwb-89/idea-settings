package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.enums.ModuleTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.util.exception.WxAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleApiController
 * @description
 * @blame wwb
 * @date 2020-11-25 00:23:56
 */
@Controller
@RequestMapping("activity/module")
public class ActivityModuleApiController {

	/**重定向到模块的地址
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 10:08:18
	 * @param request
	 * @param moduleType
	 * @param moduleId
	 * @return org.springframework.web.servlet.view.RedirectView
	*/
	@GetMapping("forward/{moduleType}/{moduleId}")
	public RedirectView urlForward(HttpServletRequest request, @PathVariable String moduleType, @PathVariable Integer moduleId) throws UnsupportedEncodingException {
		// 微信端不允许访问
		if (UserAgentUtils.isWxAccess(request)) {
			throw new WxAccessException();
		}
		// 必须要登录
		Integer uid = CookieUtils.getUid(request);
		if (uid == null) {
			// 重定向到登录页面
			String refer = request.getRequestURL().toString();
			return new RedirectView(UrlConstant.LOGIN_URL + URLEncoder.encode(refer, StandardCharsets.UTF_8.name()));
		}
		ModuleTypeEnum moduleTypeEnum = ModuleTypeEnum.fromValue(moduleType);
		String url = "";
		switch (moduleTypeEnum) {
			case WORK:
				url = getWorkAccessUrl(moduleId, request);
				break;
			case STAR:
				url = getStarAccessUrl(moduleId, request);
				break;
			case PUNCH:
				url = getPunchAccessUrl(moduleId, request);
				break;
			case TPK:
				url = getTpkAccessUrl(moduleId, request);
				break;
			default:

		}
		RedirectView redirectView = new RedirectView(url);
		return redirectView;
	}

	private String getWorkAccessUrl(Integer activityId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "https://reading.chaoxing.com/m/activity/" + activityId;
		} else {
			accessUrl = "http://reading.chaoxing.com/activity/" + activityId;
		}
		return accessUrl;
	}

	private String getStarAccessUrl(Integer starId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "https://star.chaoxing.com/app/map/" + starId + "/index";
		} else {
			accessUrl = "http://star.chaoxing.com/pc/map/" + starId + "/index";
		}
		return accessUrl;
	}

	private String getPunchAccessUrl(Integer punchId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "https://appcd.chaoxing.com/punch/" + punchId + "/handleable-detail";
		} else {
			accessUrl = "http://new.ydhd.chaoxing.com/punch-qr/" + punchId;
		}
		return accessUrl;
	}

	private String getTpkAccessUrl(Integer tpkId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "https://teacher2.chaoxing.com/tpk3-activity/?activityId=" + tpkId;
		} else {
			accessUrl = "https://teacher2.chaoxing.com/tpk3-activity/admin/statistics/activity/" + tpkId;
		}
		return accessUrl;
	}

}
