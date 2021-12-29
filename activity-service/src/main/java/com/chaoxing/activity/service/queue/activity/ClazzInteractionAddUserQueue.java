package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**班级互动添加用户队列
 * @author wwb
 * @version ver 1.0
 * @className ClazzInteractionAddUserQueue
 * @description
 * @blame wwb
 * @date 2021-12-29 18:22:33
 */
@Slf4j
@Service
public class ClazzInteractionAddUserQueue implements IQueue<ClazzInteractionAddUserQueue.QueueParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "clazz_interaction" + CacheConstant.CACHE_KEY_SEPARATOR + "add_user";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, CACHE_KEY, queueParam);
    }

    public void delayPush(QueueParamDTO queueParam) {
        delayPush(redissonClient, CACHE_KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 用户id */
        private Integer uid;
        /** 活动id */
        private Integer activityId;

    }

}
