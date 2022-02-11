package com.chaoxing.activity.service.queue.notice;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动提醒通知队列
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/11 6:34 PM
 * @version: 1.0
 */
@Slf4j
@Service
public class ActivityReminderNoticeQueue implements IQueue<ActivityReminderNoticeQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_push_reminder";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {
        /** 活动id */
        private Integer activityId;
    }
}
