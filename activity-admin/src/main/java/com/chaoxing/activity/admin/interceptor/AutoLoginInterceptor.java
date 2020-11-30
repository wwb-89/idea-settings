package com.chaoxing.activity.admin.interceptor;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.CookieValidationService;
import com.chaoxing.activity.service.LoginService;
import com.chaoxing.activity.admin.util.CookieUtils;
import com.chaoxing.activity.admin.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**自定登录
 * @author wwb
 * @version ver 1.0
 * @className AutoLoginInterceptor
 * @description
 * @blame wwb
 * @date 2020-11-12 17:35:39
 */
@Slf4j
@Component
public class AutoLoginInterceptor extends HandlerInterceptorAdapter {

	@Resource
	private LoginService loginService;
	@Resource
	private CookieValidationService cookieValidationService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("请求地址:{}", request.getRequestURI());
		validateCookieLoginUser(request);
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		if (loginUser == null) {
			// 试着登录
			String uid = CookieUtils.getUid(request);
			String fid = CookieUtils.getFid(request);
			if (StringUtils.isNotBlank(uid)) {
				long validateTime = CookieUtils.getValidateTime(request);
				String signature = CookieUtils.getSignature(request);
				if (cookieValidationService.isEffective(Integer.parseInt(uid), validateTime, signature)) {
					loginUser = loginService.login(Integer.parseInt(uid), Integer.parseInt(fid));
					LoginUtils.login(request, loginUser);
				}
			}
		}
		return super.preHandle(request, response, handler);
	}

	private void validateCookieLoginUser(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		if (loginUser != null) {
			Integer loginUid = loginUser.getUid();
			Integer loginFid = loginUser.getFid();
			String cookieUid = CookieUtils.getUid(request);
			String cookieFid = CookieUtils.getFid(request);
			if (!String.valueOf(loginUid).equals(cookieUid) || !String.valueOf(loginFid).equals(cookieFid)) {
				// uid不匹配或者切换了fid
				LoginUtils.logout(request);
				return;
			}
		}
	}

}
