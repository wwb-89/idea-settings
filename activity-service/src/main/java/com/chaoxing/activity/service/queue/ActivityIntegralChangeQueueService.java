package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**活动第二课堂积分变更队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeQueueService
 * @description 第二课堂积分变更
 * @blame wwb
 * @date 2021-03-26 21:39:36
 */
@Slf4j
@Service
public class ActivityIntegralChangeQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "second_classroom_integral_change";

	@Resource
	private RedisTemplate redisTemplate;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 21:43:21
	 * @param signId
	 * @return void
	*/
	public void add(@NotNull Integer signId) {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(QUEUE_CACHE_KEY, signId);
	}

	/**从队列中获取数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 21:44:27
	 * @param
	 * @return java.lang.Integer
	*/
	public Integer get() {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		return listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
	}

}