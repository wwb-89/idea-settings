package com.chaoxing.activity.service.queue.event;

import com.chaoxing.activity.dto.event.ActivityAboutStartEventOrigin;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**活动即将开始事件队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutStartEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 11:35:17
 */
@Slf4j
@Service
public class ActivityAboutStartEventQueue implements IDelayedQueue<ActivityAboutStartEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_about_start";
    private static final Integer ABOUT_START_HOURS = 24;

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityAboutStartEventOrigin eventOrigin) {
        remove(eventOrigin);
        LocalDateTime startTime = eventOrigin.getStartTime();
        LocalDateTime noticeTime = startTime.minusHours(ABOUT_START_HOURS);
        if (noticeTime.isBefore(LocalDateTime.now())) {
            // 通知时间已经过了，忽略
            return;
        }
        push(redissonClient, KEY, eventOrigin, noticeTime);
    }

    public ActivityAboutStartEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    private void remove(ActivityAboutStartEventOrigin eventOrigin) {
        List<ActivityAboutStartEventOrigin> eventOrigins = list(redissonClient, KEY);
        if (CollectionUtils.isEmpty(eventOrigins)) {
            return;
        }
        for (ActivityAboutStartEventOrigin origin : eventOrigins) {
            if (Objects.equals(origin.getActivityId(), eventOrigin.getActivityId())) {
                remove(redissonClient, KEY, origin);
            }
        }
    }

}