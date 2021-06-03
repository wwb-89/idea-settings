package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CommonConstant;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author wwb
 * @version ver 1.0
 * @className IQueueService
 * @description
 * @blame wwb
 * @date 2021-05-31 17:43:57
 */
public interface IQueueService<T> {

    default void push(RedissonClient redissonClient, String key, T value) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        delayedQueue.offer(value, CommonConstant.DELAYED_QUEUE_DURATION.toMillis(), TimeUnit.MILLISECONDS);
    }

    default T pop(RedissonClient redissonClient, String key) throws InterruptedException {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
        return blockingDeque.poll(CommonConstant.QUEUE_GET_WAIT_TIME.toMillis(), TimeUnit.MILLISECONDS);
    }

}
