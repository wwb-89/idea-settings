package com.chaoxing.activity.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.CookieValidationService;
import com.chaoxing.activity.service.LoginService;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.web.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;
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
		log.info("ip:{},请求的url为:{},参数:{}", HttpServletRequestUtils.getClientIp(request), request.getRequestURL().toString(), JSON.toJSONString(request.getParameterMap()));
		validateCookieLoginUser(request);
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		if (loginUser == null) {
			// 试着登录
			Integer uid = CookieUtils.getUid(request);
			Integer fid = CookieUtils.getFid(request);
			if (uid != null && fid == null) {
				fid = CommonConstant.CX_NETWORK_FID;
			}
			if (uid != null) {
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
			if (!loginUid.equals(cookieUid) || !loginFid.equals(cookieFid)) {
				// uid不匹配或者切换了fid
				LoginUtils.logout(request);
				return;
			}
		}
	}

}
