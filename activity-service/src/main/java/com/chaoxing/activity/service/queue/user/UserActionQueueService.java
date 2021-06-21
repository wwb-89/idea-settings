package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.enums.UserActionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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

    /**用户报名行为
     * @Description 
     * @author wwb
     * @Date 2021-06-18 16:58:01
     * @param queueParam
     * @return void
    */
    public void addUserSignUpAction(QueueParamDTO queueParam) {
        push(redissonClient, USER_SIGN_UP_ACTION_CACHE_KEY, queueParam);
    }

    public QueueParamDTO getUserSignUpAction() throws InterruptedException {
        return pop(redissonClient, USER_SIGN_UP_ACTION_CACHE_KEY);
    }

    /**用户签到行为
     * @Description 
     * @author wwb
     * @Date 2021-06-18 17:02:46
     * @param queueParam
     * @return void
    */
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
        /** 报名id */
        private Integer signUpId;
        /** 签到id */
        private Integer signInId;
        /** 行为 */
        private UserActionEnum userAction;
        /** 时间 */
        private LocalDateTime time;
    }

}