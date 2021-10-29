package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityReleaseEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动发布事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 15:02:31
 */
@Slf4j
@Service
public class ActivityReleaseEventQueue implements IQueue<ActivityReleaseEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_release";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityReleaseEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityReleaseEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}