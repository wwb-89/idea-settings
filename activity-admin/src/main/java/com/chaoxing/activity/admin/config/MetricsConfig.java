package com.chaoxing.activity.admin.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author wwb
 * @version ver 1.0
 * @className MetricsConfig
 * @description
 * @blame wwb
 * @date 2021-03-22 16:15:56
 */
@Configuration
public class MetricsConfig {

	@Bean
	public LoggingMeterRegistry loggingMeterRegistry() {
		return new LoggingMeterRegistry(new LoggingRegistryConfig() {

			@Override
			public Duration step() {
				// 30s输出一次
				return Duration.ofSeconds(30);
			}

			@Override
			public String get(String s) {
				return null;
			}

		}, Clock.SYSTEM);
	}

}
