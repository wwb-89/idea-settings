package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityIntegralChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动积分改变事件队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 16:37:02
 */
@Slf4j
@Service
public class ActivityIntegralChangeEventQueue implements IQueue<ActivityIntegralChangeEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_integral_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityIntegralChangeEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityIntegralChangeEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}