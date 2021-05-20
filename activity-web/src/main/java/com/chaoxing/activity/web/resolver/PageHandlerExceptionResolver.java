package com.chaoxing.activity.web.resolver;

import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.ExceptionConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.util.exception.LoginRequiredException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** 页面类异常处理
 * @author wwb
 * @version ver 1.0
 * @className PageHandlerExceptionResolver
 * @description
 * @blame wwb
 * @date 2020-08-21 22:07:40
 */
@Slf4j
@Order(-1)
@Component
public class PageHandlerExceptionResolver implements HandlerExceptionResolver {

	@SneakyThrows
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		log.error("请求url:{}, 请求参数:{}", request.getRequestURI(), request.getQueryString());
		log.error(ExceptionUtils.getStackTrace(ex));
		if (isPageRequest(handler)) {
			if (ex instanceof LoginRequiredException) {
				// 重定向到登录页面
				String url = request.getRequestURL().toString();
				String queryString = request.getQueryString();
				if (StringUtils.isNotBlank(queryString)) {
					url += "?" + queryString;
				}
				String redirectUrl = UrlConstant.LOGIN_URL + URLEncoder.encode(url, StandardCharsets.UTF_8.name());
				return new ModelAndView("redirect:" + redirectUrl);
			}
			String message = getMessage(ex);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName(getViewName(request));
			modelAndView.addObject("message", message);
			return modelAndView;
		}
		return null;
	}

	private String getViewName(HttpServletRequest request) {
		if (UserAgentUtils.isMobileAccess(request)) {
			return "/error/mobile/50x";
		} else {
			return "/error/pc/50x";
		}
	}

	private String getMessage(Exception ex) {
		if (ex == null || !(ex instanceof BusinessException)) {
			return ExceptionConstant.DEFAULT_ERROR_MESSAGE;
		}
		return ex.getMessage();
	}

	private boolean isPageRequest(Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			return AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ResponseBody.class) == null
					&& AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ResponseBody.class) == null
					&& AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RestController.class) == null;
		}
		return true;
	}


}
