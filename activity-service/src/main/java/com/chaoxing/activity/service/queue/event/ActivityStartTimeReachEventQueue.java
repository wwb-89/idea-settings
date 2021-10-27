package com.chaoxing.activity.service.queue.event;

import com.chaoxing.activity.dto.event.ActivityStartTimeReachEventOrigin;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**活动开始时间到达事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartTimeReachEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 19:58:23
 */
@Slf4j
@Service
public class ActivityStartTimeReachEventQueue implements IDelayedQueue<ActivityStartTimeReachEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_start_time_reach";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityStartTimeReachEventOrigin eventOrigin) {
        remove(eventOrigin.getActivityId());
        push(redissonClient, KEY, eventOrigin, eventOrigin.getStartTime());
    }

    public ActivityStartTimeReachEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    private void remove(Integer activityId) {
        List<ActivityStartTimeReachEventOrigin> eventOrigins = list(redissonClient, KEY);
        if (CollectionUtils.isEmpty(eventOrigins)) {
            return;
        }
        for (ActivityStartTimeReachEventOrigin eventOrigin : eventOrigins) {
            if (Objects.equals(eventOrigin.getActivityId(), activityId)) {
                remove(redissonClient, KEY, eventOrigin);
            }
        }
    }

}