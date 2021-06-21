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

/**
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryQueueService
 * @description
 * @blame wwb
 * @date 2021-05-26 09:44:58
 */
@Slf4j
@Service
public class UserStatSummaryQueueService implements IQueueService<UserStatSummaryQueueService.QueueParamDTO> {

    /** 用户签到队列缓存key */
    private static final String USER_SIGN_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "sign";
    /** 用户成绩队列缓存key */
    private static final String USER_RESULT_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "result";

    @Resource
    private RedissonClient redissonClient;

    public void addUserSignStat(QueueParamDTO queueParam) {
        push(redissonClient, USER_SIGN_CACHE_KEY, queueParam);
    }

    public QueueParamDTO getUserSignStat() throws InterruptedException {
        return pop(redissonClient, USER_SIGN_CACHE_KEY);
    }

    public void addUserResultStat(QueueParamDTO queueParam) {
        push(redissonClient, USER_RESULT_CACHE_KEY, queueParam);
    }

    public QueueParamDTO getUserResultStata() throws InterruptedException {
        return pop(redissonClient, USER_RESULT_CACHE_KEY);
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