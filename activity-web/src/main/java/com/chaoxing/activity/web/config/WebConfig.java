package com.chaoxing.activity.web.config;

import com.chaoxing.activity.web.interceptor.AutoLoginInterceptor;
import com.chaoxing.activity.web.interceptor.LoginRequiredInterceptor;
import com.chaoxing.activity.web.interceptor.LoginUserValidateInterceptor;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className WebConfig
 * @description
 * @blame wwb
 * @date 2020-11-09 11:22:26
 */
@Configuration
public class WebConfig implements WebMvcConfigurer, ErrorPageRegistrar {

	@Resource
	private LoginUserValidateInterceptor loginUserValidateInterceptor;
	@Resource
	private AutoLoginInterceptor autoLoginInterceptor;
	@Resource
	private LoginRequiredInterceptor loginRequiredInterceptor;

	private List<String> listStaticResourcePathPatterns() {
		return new ArrayList(){{
			add("/favicon.ico");
			add("/assets/**");
			add("/pc/**");
			add("/mobile/**");
		}};
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> staticResourcePathPatterns = listStaticResourcePathPatterns();
		registry.addInterceptor(loginUserValidateInterceptor).addPathPatterns("/**").excludePathPatterns(staticResourcePathPatterns);
		registry.addInterceptor(autoLoginInterceptor).addPathPatterns("/**").excludePathPatterns(staticResourcePathPatterns);
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/**").excludePathPatterns(staticResourcePathPatterns);
	}

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
	}

}