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
public interface IQueue<T> {

    /**加入队列
     * @Description 
     * @author wwb
     * @Date 2021-06-28 14:52:48
     * @param redissonClient
     * @param key
     * @param value
     * @return void
    */
    default void push(RedissonClient redissonClient, String key, T value) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        delayedQueue.offer(value, CommonConstant.DELAYED_QUEUE_DURATION.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**延时推送
     * @Description 
     * @author wwb
     * @Date 2021-12-01 10:08:45
     * @param redissonClient
     * @param key
     * @param value
     * @return void
    */
    default void delayPush(RedissonClient redissonClient, String key, T value) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        delayedQueue.offer(value, CommonConstant.FAIL_DELAYED_QUEUE_DURATION.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**从队列中获取
     * @Description 
     * @author wwb
     * @Date 2021-06-28 14:52:58
     * @param redissonClient
     * @param key
     * @throws InterruptedException
     * @return T
    */
    default T pop(RedissonClient redissonClient, String key) throws InterruptedException {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
        return blockingDeque.poll(CommonConstant.QUEUE_GET_WAIT_TIME.toMillis(), TimeUnit.MILLISECONDS);
    }

}
