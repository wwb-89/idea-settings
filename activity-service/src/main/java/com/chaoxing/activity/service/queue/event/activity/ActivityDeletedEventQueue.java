package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityDeletedEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityDeletedEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 17:44:53
 */
@Slf4j
@Service
public class ActivityDeletedEventQueue implements IQueue<ActivityDeletedEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_deleted";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityDeletedEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityDeletedEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
