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

/**活动已报名用户发送通知
 * @author wwb
 * @version ver 1.0
 * @className ActivitySignedUpUserNoticeQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 12:14:47
 */
@Slf4j
@Service
public class ActivitySignedUpUserNoticeQueue implements IQueue<ActivitySignedUpUserNoticeQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_signed_up_user";

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
        /** 通知标题 */
        private String title;
        /** 通知内容 */
        private String content;
        /** 附件 */
        private String attachment;

    }

}