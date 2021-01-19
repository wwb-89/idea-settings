package com.chaoxing.activity.admin.interceptor;

import com.chaoxing.activity.admin.util.CookieUtils;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.CookieValidationService;
import com.chaoxing.activity.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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
			Integer uid = CookieUtils.getUid(request);
			Integer fid = CookieUtils.getFid(request);
			if (uid != null && fid != null) {
				long validateTime = CookieUtils.getValidateTime(request);
				String signature = CookieUtils.getSignature(request);
				if (cookieValidationService.isEffective(uid, validateTime, signature)) {
					loginUser = loginService.login(uid, fid);
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
			Integer cookieUid = CookieUtils.getUid(request);
			Integer cookieFid = CookieUtils.getFid(request);
			if (!Objects.equals(loginUid, cookieUid) || !Objects.equals(loginFid, cookieFid)) {
				// uid不匹配或者切换了fid
				LoginUtils.logout(request);
			}
		}
	}

}
