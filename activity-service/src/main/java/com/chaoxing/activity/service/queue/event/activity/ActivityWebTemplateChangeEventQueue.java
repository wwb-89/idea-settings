package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityWebTemplateChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityWebTemplateChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 18:45:23
 */
@Slf4j
@Service
public class ActivityWebTemplateChangeEventQueue implements IQueue<ActivityWebTemplateChangeEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_web_template_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityWebTemplateChangeEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityWebTemplateChangeEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
