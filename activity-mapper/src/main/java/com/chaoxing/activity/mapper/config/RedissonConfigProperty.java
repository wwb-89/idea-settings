package com.chaoxing.activity.mapper.config;

import lombok.Data;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wwb
 * @version ver 1.0
 * @className RedissonConfigProperty
 * @description
 * @blame wwb
 * @date 2021-11-01 18:18:02
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis.redisson.config")
public class RedissonConfigProperty {

    private SingleServerConfig singleServerConfig;
    private Integer threads = 2 * 4;
    private Integer nettyThreads = 2 * 4;

}
