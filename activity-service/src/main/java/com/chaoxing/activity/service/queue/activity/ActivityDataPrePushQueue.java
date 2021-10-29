package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动数据预推送队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataPrePushQueue
 * @description
 * @blame wwb
 * @date 2021-10-29 16:39:58
 */
@Slf4j
@Service
public class ActivityDataPrePushQueue implements IQueue<Integer> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_data_pre_push";

    @Resource
    private RedissonClient redissonClient;

    public void push(Integer activityId) {
        push(redissonClient, KEY, activityId);
    }

    public Integer pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}