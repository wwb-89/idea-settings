package com.chaoxing.activity.mapper.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className RedissonConfig
 * @description
 * @blame wwb
 * @date 2021-05-31 10:52:01
 */
@Configuration
public class RedissonConfig {

    @Resource
    private RedissonConfigProperty redissonConfigProperty;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.setThreads(redissonConfigProperty.getThreads());
        config.setNettyThreads(redissonConfigProperty.getNettyThreads());
        SingleServerConfig singleServerConfig = config.useSingleServer();
        SingleServerConfig serverConfig = redissonConfigProperty.getSingleServerConfig();
        singleServerConfig.setAddress(serverConfig.getAddress());
        singleServerConfig.setPassword(serverConfig.getPassword());
        singleServerConfig.setDatabase(serverConfig.getDatabase());
        singleServerConfig.setIdleConnectionTimeout(serverConfig.getIdleConnectionTimeout());
        singleServerConfig.setConnectTimeout(serverConfig.getConnectTimeout());
        singleServerConfig.setTimeout(serverConfig.getTimeout());
        singleServerConfig.setRetryAttempts(serverConfig.getRetryAttempts());
        singleServerConfig.setRetryInterval(serverConfig.getRetryInterval());
        singleServerConfig.setSubscriptionsPerConnection(serverConfig.getSubscriptionsPerConnection());
        singleServerConfig.setSubscriptionConnectionMinimumIdleSize(serverConfig.getSubscriptionConnectionMinimumIdleSize());
        singleServerConfig.setSubscriptionConnectionPoolSize(serverConfig.getSubscriptionConnectionPoolSize());
        singleServerConfig.setConnectionMinimumIdleSize(serverConfig.getConnectionMinimumIdleSize());
        singleServerConfig.setConnectionPoolSize(serverConfig.getConnectionPoolSize());
        config.setCodec(new FastjsonCodec());
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
