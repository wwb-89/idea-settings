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

/**活动名称改变通知服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameChangeNoticeQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 15:01:17
 */
@Slf4j
@Service
public class ActivityNameChangeNoticeQueueService implements IQueueService<Integer> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_name_change";

	@Resource
	private RedissonClient redissonClient;

	/**新增队列数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:07:29
	 * @param activityId
	 * @return void
	*/
	public void push(Integer activityId) {
		push(redissonClient, QUEUE_CACHE_KEY, activityId);
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:12:31
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer pop() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

}