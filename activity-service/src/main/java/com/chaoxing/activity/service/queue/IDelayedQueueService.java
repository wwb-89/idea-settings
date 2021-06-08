package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CommonConstant;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**延时队列
 * @author wwb
 * @version ver 1.0
 * @className IDelayedQueueService
 * @description
 * @blame wwb
 * @date 2021-06-08 10:24:43
 */
public interface IDelayedQueueService<T> {

	default void push(RedissonClient redissonClient, String key, T value, LocalDateTime time) {
		RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue(key);
		RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
		// 计算和当前时间的时间差（小于0则使用通用的延时）
		Duration delay = CommonConstant.DELAYED_QUEUE_DURATION;
		LocalDateTime now = LocalDateTime.now();
		if (now.isBefore(time)) {
			delay = Duration.between(time, now);
		}
		delayedQueue.offer(value, delay.toMillis(), TimeUnit.MILLISECONDS);
	}

	default T pop(RedissonClient redissonClient, String key) throws InterruptedException {
		RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
		return blockingDeque.poll(CommonConstant.QUEUE_GET_WAIT_TIME.toMillis(), TimeUnit.MILLISECONDS);
	}

}
