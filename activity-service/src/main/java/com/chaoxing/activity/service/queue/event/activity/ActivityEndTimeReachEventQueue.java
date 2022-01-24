package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityEndTimeReachEventOrigin;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动结束事件到达事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndTimeReachEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 19:59:05
 */
@Slf4j
@Service
public class ActivityEndTimeReachEventQueue implements IDelayedQueue<ActivityEndTimeReachEventOrigin> {

    public static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_end_time_reach";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityEndTimeReachEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin, eventOrigin.getEndTime());
    }

    public void rePush(ActivityEndTimeReachEventOrigin eventOrigin) {
        rePush(redissonClient, KEY, eventOrigin, eventOrigin.getEndTime());
    }

    public ActivityEndTimeReachEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}