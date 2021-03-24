package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignQueueService
 * @description
 * @blame wwb
 * @date 2021-03-24 13:04:26
 */
@Slf4j
@Service
public class SignQueueService {

	private static final String NOTICE_RATING_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice" + CacheConstant.CACHE_KEY_SEPARATOR + "rating";
	private static final String VALUE_TYPE_SEPARATOR = "#";

	@Resource
	private RedisTemplate redisTemplate;

	/**新增通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 13:59:24
	 * @param signId
	 * @param uid
	 * @return void
	*/
	public void add(Integer signId, Integer uid) {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(NOTICE_RATING_QUEUE_CACHE_KEY, generateValue(signId, uid));
	}

	/**获取队列的数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:04:48
	 * @param 
	 * @return java.lang.String
	*/
	public String get() {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		String value = listOperations.rightPop(NOTICE_RATING_QUEUE_CACHE_KEY, Duration.ofMillis(1));
		return value;
	}

	/**生成值
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:01:58
	 * @param signId
	 * @param uid
	 * @return java.lang.String
	*/
	private String generateValue(Integer signId, Integer uid) {
		return signId + VALUE_TYPE_SEPARATOR + uid;
	}

	/**从值中获取报名签到id
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:01:40
	 * @param value
	 * @return java.lang.Integer
	*/
	public Integer getSignIdFromValue(String value) {
		return resolveValue(value, 0);
	}

	/**从值中获取uid
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:01:34
	 * @param value
	 * @return java.lang.Integer
	*/
	public Integer getUidFromValue(String value) {
		return resolveValue(value, 1);
	}

	/**从值中获取指定的值
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:01:18
	 * @param value
	 * @param index`
	 * @return java.lang.Integer
	*/
	private Integer resolveValue(String value, Integer index) {
		if (StringUtils.isBlank(value) || !value.contains(VALUE_TYPE_SEPARATOR)) {
			return null;
		}
		String[] split = value.split(VALUE_TYPE_SEPARATOR);
		return Integer.parseInt(split[index]);
	}

}