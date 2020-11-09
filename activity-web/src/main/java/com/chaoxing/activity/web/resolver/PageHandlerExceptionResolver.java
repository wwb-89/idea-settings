package com.chaoxing.activity.web.resolver;

import com.chaoxing.activity.util.constant.ExceptionConstant;
import com.chaoxing.activity.util.exception.BusinessException;
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

/** 页面类异常处理
 * @author wwb
 * @version ver 1.0
 * @className PageHandlerExceptionResolver
 * @description
 * @blame wwb
 * @date 2020-08-21 22:07:40
 */
@Order(-1)
@Component
public class PageHandlerExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if (isPageRequest(handler)) {
			String message = getMessage(ex);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("/error/50x");
			modelAndView.addObject("message", message);
			return modelAndView;
		}
		return null;
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
