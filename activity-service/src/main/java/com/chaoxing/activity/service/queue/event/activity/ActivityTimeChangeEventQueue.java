package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动时间改变事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimeChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 18:27:05
 */
@Slf4j
@Service
public class ActivityTimeChangeEventQueue implements IQueue<ActivityTimeChangeEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_time_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityTimeChangeEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityTimeChangeEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
