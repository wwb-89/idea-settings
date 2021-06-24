package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**活动第二课堂积分变更队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeQueueService
 * @description 活动积分变更通知表单积分修改
 * @blame wwb
 * @date 2021-03-26 21:39:36
 */
@Slf4j
@Service
public class ActivityIntegralChangeQueueService implements IQueueService<Integer> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "second_classroom_integral_change";

	@Resource
	private RedissonClient redissonClient;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 21:43:21
	 * @param signId
	 * @return void
	*/
	public void push(@NotNull Integer signId) {
		push(redissonClient, QUEUE_CACHE_KEY, signId);
	}

	/**从队列中获取数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 21:44:27
	 * @param
	 * @return java.lang.Integer
	*/
	public Integer pop() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

}