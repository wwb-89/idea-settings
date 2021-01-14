package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.service.auth.ApiParamAuthService;
import com.chaoxing.activity.service.manager.PassportApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**登录代理
 * @author wwb
 * @version ver 1.0
 * @className LoginProxyApiController
 * @description
 * @blame wwb
 * @date 2021-01-13 16:01:53
 */
@Controller
@RequestMapping("api/login/proxy")
public class LoginProxyApiController {

	@Resource
	private PassportApiService passportApiService;
	@Resource
	private ApiParamAuthService apiParamAuthService;

	/**代理登录
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 16:31:17
	 * @param response
	 * @param uid
	 * @param fid
	 * @param enc
	 * @param refer
	 * @return java.lang.Object
	*/
	@RequestMapping("")
	public Object login(HttpServletResponse response, Integer uid, Integer fid, String enc, @RequestParam String refer) {
		// 验证
		apiParamAuthService.loginProxyAuth(uid, fid, enc);
		passportApiService.avoidCloseLogin(uid, fid, response);
		return new RedirectView(refer);
	}

}