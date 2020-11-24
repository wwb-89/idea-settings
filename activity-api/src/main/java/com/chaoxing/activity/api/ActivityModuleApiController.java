package com.chaoxing.activity.api;

import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.enums.ModuleTypeEnum;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleApiController
 * @description
 * @blame wwb
 * @date 2020-11-25 00:23:56
 */
@RestController
@RequestMapping("activity/module")
public class ActivityModuleApiController {

	@GetMapping("forward/{moduleType}/{moduleId}")
	public RedirectView urlForward(HttpServletRequest request, @PathVariable String moduleType, @PathVariable Integer moduleId) {
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

	private String getWorkAccessUrl(Integer workId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "";
		} else {
			accessUrl = "";
		}
		return accessUrl;
	}

	private String getStarAccessUrl(Integer starId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "";
		} else {
			accessUrl = "";
		}
		return accessUrl;
	}

	private String getPunchAccessUrl(Integer punchId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "";
		} else {
			accessUrl = "";
		}
		return accessUrl;
	}

	private String getTpkAccessUrl(Integer tpkId, HttpServletRequest request) {
		boolean mobileAccess = UserAgentUtils.isMobileAccess(request);
		String accessUrl;
		if (mobileAccess) {
			accessUrl = "";
		} else {
			accessUrl = "";
		}
		return accessUrl;
	}

}
