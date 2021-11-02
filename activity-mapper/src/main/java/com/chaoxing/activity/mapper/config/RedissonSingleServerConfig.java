package com.chaoxing.activity.mapper.config;

import lombok.Data;

/**
 * @author wwb
 * @version ver 1.0
 * @className RedissonSingleServerConfig
 * @description
 * @blame wwb
 * @date 2021-11-01 19:28:53
 */
@Data
public class RedissonSingleServerConfig {

    private String host = "127.0.0.1";
    private Integer port = 6379;
    private String password = "";
    private Integer database = 0;
    private Integer idleConnectionTimeout = 10000;
    private Integer connectTimeout = 10000;
    private Integer timeout = 3000;
    private Integer retryAttempts = 3;
    private Integer retryInterval = 1500;
    private Integer subscriptionsPerConnection = 5;
    private Integer subscriptionConnectionMinimumIdleSize = 1;
    private Integer subscriptionConnectionPoolSize = 50;
    private Integer connectionMinimumIdleSize = 32;
    private Integer connectionPoolSize = 128;

}
