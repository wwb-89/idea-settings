package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityNameChangedEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动名称改变事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 18:33:12
 */
@Slf4j
@Service
public class ActivityNameChangeEventQueue implements IQueue<ActivityNameChangedEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_name_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityNameChangedEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public ActivityNameChangedEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
