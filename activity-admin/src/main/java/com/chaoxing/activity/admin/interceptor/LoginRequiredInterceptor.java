package com.chaoxing.activity.admin.interceptor;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author wwb
 * @version ver 1.0
 * @className LoginRequiredInterceptor
 * @description
 * @blame wwb
 * @date 2020-11-27 16:26:03
 */
@Component
public class LoginRequiredInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		if (loginUser == null) {
			handleNotLogin(request, response);
			return false;
		}
		return super.preHandle(request, response, handler);
	}

	private void handleNotLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 编码refer的地址
		String refer = request.getRequestURL().toString();
		String encodedRefer = URLEncoder.encode(refer, StandardCharsets.UTF_8.name());
		response.sendRedirect("http://v1.chaoxing.com/backSchool/toLogin?refer=" + encodedRefer);
	}

}