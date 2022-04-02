package com.chaoxing.activity;

import com.chaoxing.activity.util.CustomAnnotationBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * @className ActivityTaskApplication
 * @description 
 * @author wwb
 * @blame wwb
 * @date 2022-04-02 17:40:32
 * @version ver 1.0
 */
@SpringBootApplication
@ComponentScan(nameGenerator = CustomAnnotationBeanNameGenerator.class)
@EnableCaching
public class ActivityTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityTaskApplication.class, args);
	}

}
