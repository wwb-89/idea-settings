package com.chaoxing.activity.admin.interceptor;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.util.constant.UrlConstant;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
			if (!(handler instanceof HandlerMethod)) {
				return true;
			}
			HandlerMethod method = (HandlerMethod) handler;
			handleNotLogin(request, response, method);
			return false;
		}
		return super.preHandle(request, response, handler);
	}

	private void handleNotLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod method) throws IOException {
		String refer = request.getRequestURL().toString() + "?" + request.getQueryString();
		refer = URLEncoder.encode(refer, StandardCharsets.UTF_8.name());
		if (method == null) {
			// 页面
			response.sendRedirect(UrlConstant.MANAGE_LOGIN_URL + refer);
		}else{
			if (AnnotationUtils.findAnnotation(method.getMethod(), ResponseBody.class) != null
					|| AnnotationUtils.findAnnotation(method.getBeanType(), ResponseBody.class) != null
					|| AnnotationUtils.findAnnotation(method.getBeanType(), RestController.class) != null) {
				// rest
				response.setCharacterEncoding("utf-8");
				response.setContentType("application/json; charset=utf-8");
				PrintWriter writer = response.getWriter();
				RestRespDTO restResDTO = RestRespDTO.error("请登录后操作");
				writer.write(JSON.toJSONString(restResDTO));
			} else {
				// 页面
				response.sendRedirect(UrlConstant.MANAGE_LOGIN_URL + refer);
			}
		}
	}

}