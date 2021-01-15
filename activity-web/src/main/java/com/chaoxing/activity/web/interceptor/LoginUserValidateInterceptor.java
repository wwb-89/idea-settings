package com.chaoxing.activity.web.interceptor;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.CookieValidationService;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**登录用户验证
 * @author wwb
 * @version ver 1.0
 * @className LoginUserValidateInterceptor
 * @description 登录用户如果cookie校验不通过则退出登录
 * @blame wwb
 * @date 2020-11-18 10:20:47
 */
@Component
public class LoginUserValidateInterceptor extends HandlerInterceptorAdapter {

	@Resource
	private CookieValidationService cookieValidationService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		if (loginUser != null) {
			Integer uid = CookieUtils.getUid(request);
			if (uid != null) {
				long validateTime = CookieUtils.getValidateTime(request);
				String signature = CookieUtils.getSignature(request);
				if (!cookieValidationService.isEffective(uid, validateTime, signature)) {
					LoginUtils.logout(request);
				}
			}
		}
		return super.preHandle(request, response, handler);
	}
}
