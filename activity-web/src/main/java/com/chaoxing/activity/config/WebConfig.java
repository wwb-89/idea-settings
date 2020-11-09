package com.chaoxing.activity.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/custom/404"));
		registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/custom"));
	}

}
