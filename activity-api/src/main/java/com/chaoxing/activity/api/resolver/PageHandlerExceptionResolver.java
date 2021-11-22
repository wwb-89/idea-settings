package com.chaoxing.activity.api.resolver;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.ExceptionConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.util.exception.WfwFormActivityNotGeneratedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
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
@Slf4j
@Order(-1)
@Component
public class PageHandlerExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		log.error("请求url:{}, 请求参数:{}", request.getRequestURI(), request.getQueryString());
		log.error(ExceptionUtils.getStackTrace(ex));
		if (isPageRequest(handler)) {
			String message = getMessage(ex);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName(getViewName(request, ex));
			modelAndView.addObject("message", message);
			return modelAndView;
		} else if (ex instanceof BindException){
			BindException e = (BindException) ex;
			FieldError fieldError = e.getBindingResult().getFieldError();
			RestRespDTO restResp = RestRespDTO.error(fieldError.getDefaultMessage());
			FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
			fastJsonJsonView.setExtractValueFromSingleKeyModel(true);
			ModelAndView modelAndView = new ModelAndView(fastJsonJsonView);
			modelAndView.addObject(restResp);
			return modelAndView;
		}
		return null;
	}

	private String getViewName(HttpServletRequest request, Exception e) {
		if (UserAgentUtils.isMobileAccess(request)) {
			if (e instanceof WfwFormActivityNotGeneratedException) {
				return "error/mobile/activity-not-generated";
			} else {
				return "error/mobile/50x";
			}
		}
		if (e instanceof WfwFormActivityNotGeneratedException) {
			return "error/pc/activity-not-generated";
		} else {
			return "error/pc/50x";
		}
	}

	private String getMessage(Exception ex) {
		if (ex == null || !(ex instanceof BusinessException)) {
			return ExceptionConstant.DEFAULT_ERROR_MESSAGE;
		} else if (ex instanceof  BindException) {
			BindException e = (BindException) ex;
			FieldError fieldError = e.getBindingResult().getFieldError();
			return fieldError.getDefaultMessage();
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
