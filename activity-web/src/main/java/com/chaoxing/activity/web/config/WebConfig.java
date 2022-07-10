package com.chaoxing.activity.web.config;

import com.chaoxing.activity.web.interceptor.AutoLoginInterceptor;
import com.chaoxing.activity.web.interceptor.LoginRequiredInterceptor;
import com.chaoxing.activity.web.interceptor.LoginUserValidateInterceptor;
import com.google.common.collect.Lists;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
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

	private static final List<String> EXCLUDE_PATHS = Lists.newArrayList();

	static {
		EXCLUDE_PATHS.addAll(Lists.newArrayList("/favicon.ico", "/assets/**", "/pc/**", "/mobile/**"));
		EXCLUDE_PATHS.addAll(Lists.newArrayList("/api/outer/**"));
		EXCLUDE_PATHS.addAll(Lists.newArrayList("/proxy/**"));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginUserValidateInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATHS);
		registry.addInterceptor(autoLoginInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATHS);
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATHS);
	}

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
	}

}