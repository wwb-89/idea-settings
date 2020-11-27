package com.chaoxing.activity;

import com.chaoxing.activity.util.CustomAnnotationBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan(nameGenerator = CustomAnnotationBeanNameGenerator.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableCaching
public class ActivityAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityAdminApplication.class, args);
	}

}
