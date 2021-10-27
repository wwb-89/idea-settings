package com.chaoxing.activity.service.queue.event;

import com.chaoxing.activity.dto.event.ActivityEndTimeReachEventOrigin;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_end_time_reach";

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityEndTimeReachEventOrigin eventOrigin) {
        remove(eventOrigin.getActivityId());
        push(redissonClient, KEY, eventOrigin, eventOrigin.getEndTime());
    }

    public ActivityEndTimeReachEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    private void remove(Integer activityId) {
        List<ActivityEndTimeReachEventOrigin> eventOrigins = list(redissonClient, KEY);
        if (CollectionUtils.isEmpty(eventOrigins)) {
            return;
        }
        for (ActivityEndTimeReachEventOrigin eventOrigin : eventOrigins) {
            if (Objects.equals(eventOrigin.getActivityId(), activityId)) {
                remove(redissonClient, KEY, eventOrigin);
            }
        }
    }

}