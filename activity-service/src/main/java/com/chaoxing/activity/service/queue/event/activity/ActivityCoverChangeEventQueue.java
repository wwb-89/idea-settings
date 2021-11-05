package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityCoverChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动封面改变事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 18:18:04
 */
@Slf4j
@Service
public class ActivityCoverChangeEventQueue implements IQueue<ActivityCoverChangeEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_cover_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityCoverChangeEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityCoverChangeEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
