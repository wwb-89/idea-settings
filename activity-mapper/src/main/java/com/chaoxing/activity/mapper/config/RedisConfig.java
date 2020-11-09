package com.chaoxing.activity.mapper.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.chaoxing.activity.util.constant.CacheConstant;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author wwb
 * @version ver 1.0
 * @className RedisConfig
 * @description
 * @blame wwb
 * @date 2020-11-09 11:19:17
 */
public class RedisConfig {

	@Bean
	public RedisSerializer fastJson2JsonRedisSerializer() {
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		return new FastJson2JsonRedisSerializer(Object.class);
	}

	@Bean("redisTemplate")
	public RedisTemplate redisTemplate(RedissonConnectionFactory factory, RedisSerializer fastJson2JsonRedisSerializer){
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(factory);

		RedisSerializer keySerializer = new StringRedisSerializer();
		redisTemplate.setHashKeySerializer(keySerializer);
		redisTemplate.setKeySerializer(keySerializer);

		redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean("redisTemplateTx")
	public RedisTemplate redisTemplateTx(RedissonConnectionFactory factory, RedisSerializer fastJson2JsonRedisSerializer){
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(factory);

		RedisSerializer keySerializer = new StringRedisSerializer();
		redisTemplate.setHashKeySerializer(keySerializer);
		redisTemplate.setKeySerializer(keySerializer);

		redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);

		//redis   开启事务
		//开启事务会导致连接池中的连接会有不一致的情况
		redisTemplate.setEnableTransactionSupport(true);

		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public CacheManager cacheManager(RedissonConnectionFactory connectionFactory, RedisSerializer valueSerializer) {
		CacheKeyPrefix cacheKeyPrefix = prefix -> prefix + CacheConstant.CACHE_KEY_SEPARATOR;
		RedisSerializer keySerializer = new StringRedisSerializer();
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
				.entryTtl(Duration.ofHours(1))
				.disableCachingNullValues()
				.computePrefixWith(cacheKeyPrefix);

		RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(redisCacheConfiguration)
				.transactionAware()
				.build();
		return redisCacheManager;
	}

}