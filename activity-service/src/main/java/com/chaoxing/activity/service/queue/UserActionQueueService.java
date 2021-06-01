package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户报名签到行为队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserSignActionQueueService
 * @description
 * @blame wwb
 * @date 2021-05-25 11:16:38
 */
@Slf4j
@Service
public class UserActionQueueService implements IQueueService<UserActionQueueService.QueueParamDTO> {

    /** 报名行为 */
    private static final String USER_SIGN_UP_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_sign_up_action";
    /** 签到行为 */
    private static final String USER_SIGN_IN_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_sign_in_action";
    /** 成绩行为 */
    private static final String USER_RESULT_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_result_action";

    @Resource
    private RedissonClient redissonClient;

    public void addUserSignUpAction(QueueParamDTO queueParam) {
        push(redissonClient, USER_SIGN_UP_ACTION_CACHE_KEY, queueParam);
    }

    public QueueParamDTO getUserSignUpAction() throws InterruptedException {
        return pop(redissonClient, USER_SIGN_UP_ACTION_CACHE_KEY);
    }

    public void addUserSignInAction(QueueParamDTO queueParam) {
        push(redissonClient, USER_SIGN_IN_ACTION_CACHE_KEY, queueParam);
    }

    public QueueParamDTO getUserSignInAction() throws InterruptedException {
        return pop(redissonClient, USER_SIGN_IN_ACTION_CACHE_KEY);
    }

    public void addUserResultAction(QueueParamDTO queueParam) {
        push(redissonClient, USER_RESULT_ACTION_CACHE_KEY, queueParam);
    }

    public QueueParamDTO getUserResultAction() throws InterruptedException {
        return pop(redissonClient, USER_RESULT_ACTION_CACHE_KEY);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 用户id */
        private Integer uid;
        /** 报名签到id */
        private Integer signId;

    }

}