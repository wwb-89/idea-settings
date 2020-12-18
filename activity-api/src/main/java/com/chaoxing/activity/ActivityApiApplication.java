package com.chaoxing.activity;

import com.chaoxing.activity.util.CustomAnnotationBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityApiApplication
 * @description
 * @blame wwb
 * @date 2020-12-04 13:53:23
 */
@SpringBootApplication
@ComponentScan(nameGenerator = CustomAnnotationBeanNameGenerator.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableCaching
public class ActivityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityApiApplication.class, args);
	}

}