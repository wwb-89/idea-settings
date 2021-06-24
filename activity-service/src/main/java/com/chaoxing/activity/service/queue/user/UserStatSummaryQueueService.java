package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户活动统计汇总数据更新队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryQueueService
 * @description 用户报名签到行为、用户成绩变更会将数据推送到该队列
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

    public void pushUserSignStat(QueueParamDTO queueParam) {
        push(redissonClient, USER_SIGN_CACHE_KEY, queueParam);
    }

    public QueueParamDTO popUserSignStat() throws InterruptedException {
        return pop(redissonClient, USER_SIGN_CACHE_KEY);
    }

    public void pushUserResultStat(QueueParamDTO queueParam) {
        push(redissonClient, USER_RESULT_CACHE_KEY, queueParam);
    }

    public QueueParamDTO popUserResultStata() throws InterruptedException {
        return pop(redissonClient, USER_RESULT_CACHE_KEY);
    }

    @Data
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 用户id */
        private Integer uid;
        /** 活动id */
        private Integer activityId;

    }

}