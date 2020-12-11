package com.chaoxing.activity.admin.controller;

import com.chaoxing.activity.util.UserAgentUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className ErrorController
 * @description
 * @blame wwb
 * @date 2020-12-11 16:42:09
 */
@Controller
@RequestMapping("error")
public class ErrorController {

	/**页面不存在
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-11 16:43:02
	 * @param request
	 * @return java.lang.String
	*/
	@RequestMapping("404")
	public String notFound(HttpServletRequest request) {
		if (UserAgentUtils.isMobileAccess(request)) {
			return "/error/mobile/404";
		} else {
			return "/error/pc/404";
		}
	}

}
