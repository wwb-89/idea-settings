package com.chaoxing.activity.mapper.config;

import com.alibaba.fastjson.parser.ParserConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.timeout}")
    private Integer timeout;

    @Bean
    public RedissonClient redissonClient() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://"+ host +":" + port);
        singleServerConfig.setPassword(password);
        singleServerConfig.setDatabase(database);
        singleServerConfig.setTimeout(timeout);
        Codec codec = new FastjsonCodec();
        config.setCodec(codec);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
