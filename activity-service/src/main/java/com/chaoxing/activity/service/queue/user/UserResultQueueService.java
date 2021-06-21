package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户成绩队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultQueueService
 * @description
 * @blame wwb
 * @date 2021-06-21 16:58:27
 */
@Slf4j
@Service
public class UserResultQueueService implements IQueueService<UserResultQueueService.QueueParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_result";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, CACHE_KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 用户id */
        private Integer uid;
        /** 活动id */
        private Integer activityId;

    }

}
