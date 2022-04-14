package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityCreditChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动学分改变事件队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreditChangeEventQueue
 * @description
 * @blame wwb
 * @date 2022-01-13 17:21:35
 */
@Slf4j
@Service
public class ActivityCreditChangeEventQueue implements IQueue<ActivityCreditChangeEventOrigin> {

	private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_credit_change";

	@Resource
	private RedissonClient redissonClient;

	public void push(ActivityCreditChangeEventOrigin eventOrigin) {
		push(redissonClient, KEY, eventOrigin);
	}

	public ActivityCreditChangeEventOrigin pop() throws InterruptedException {
		return pop(redissonClient, KEY);
	}

}