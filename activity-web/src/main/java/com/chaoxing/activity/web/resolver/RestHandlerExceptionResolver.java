package com.chaoxing.activity.web.resolver;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.util.constant.ExceptionConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wwb
 * @version ver 1.0
 * @className RestHandlerExceptionResolver
 * @description
 * @blame wwb
 * @date 2019-10-22 16:28:26
 */
@Slf4j
@Component
public class RestHandlerExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		log.error("{}", ex);
		// 是Rest请求 并且 接受该异常类型
		if(isRestRequest(handler) && isAcceptException(ex)){
			// 直接通过Response将结果写回
			String errorMessage;
			if (ex instanceof BusinessException) {
				errorMessage = ex.getMessage();
			} else {
				errorMessage = ExceptionConstant.DEFAULT_ERROR_MESSAGE;
			}
			RestRespDTO<Void> restResp = RestRespDTO.error(errorMessage);
			FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
			fastJsonJsonView.setExtractValueFromSingleKeyModel(true);
			ModelAndView modelAndView = new ModelAndView(fastJsonJsonView);
			modelAndView.addObject(restResp);
			return modelAndView;
		}
		// 其他类型，应用下一个处理器
		return null;
	}

	/**是不是rest请求
	 * @Description 判断Controller和Method上是否有@ResponseBody注解
	 * @author wwb
	 * @Date 2018-12-20 12:55:35
	 * @param handler
	 * @return boolean
	 */
	private boolean isRestRequest(Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			return AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ResponseBody.class) != null
					|| AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ResponseBody.class) != null
					|| AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RestController.class) != null;
		}
		return false;
	}

	/**是否是可处理的异常
	 * @Description 接受BusinessException的所有子类
	 * @author wwb
	 * @Date 2018-12-20 12:55:05
	 * @param e
	 * @return boolean
	 */
	private boolean isAcceptException(Exception e) {
		return true;
	}

}
