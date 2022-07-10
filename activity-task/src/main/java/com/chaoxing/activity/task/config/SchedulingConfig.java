package com.chaoxing.activity.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author wwb
 * @version ver 1.0
 * @className SchedulingConfig
 * @description
 * @blame wwb
 * @date 2021-02-03 19:36:00
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(100);
		return taskScheduler;
	}

}
