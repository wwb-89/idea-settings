package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
public class UserActionQueueService {

    /** 报名行为 */
    private static final String USER_SIGN_UP_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_sign_up_action";
    /** 签到行为 */
    private static final String USER_SIGN_IN_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_sign_in_action";
    /** 成绩行为 */
    private static final String USER_RESULT_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_result_action";

    @Resource
    private RedisTemplate redisTemplate;

    public void addUserSignUpAction(Integer uid, Integer signId) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(USER_SIGN_UP_ACTION_CACHE_KEY, UserActionDTO.builder()
                .uid(uid)
                .signId(signId)
                .build());
    }

    public UserActionDTO getUserSignUpAction() {
        ListOperations<String, UserActionDTO> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(USER_SIGN_UP_ACTION_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

    public void addUserSignInAction(Integer uid, Integer signId) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(USER_SIGN_IN_ACTION_CACHE_KEY, UserActionDTO.builder()
                .uid(uid)
                .signId(signId)
                .build());
    }

    public UserActionDTO getUserSignInAction() {
        ListOperations<String, UserActionDTO> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(USER_SIGN_IN_ACTION_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

    public void addUserResultAction(Integer uid, Integer signId) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(USER_RESULT_ACTION_CACHE_KEY, UserActionDTO.builder()
                .uid(uid)
                .signId(signId)
                .build());
    }

    public UserActionDTO getUserResultAction() {
        ListOperations<String, UserActionDTO> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(USER_RESULT_ACTION_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActionDTO {

        /** uid */
        private Integer uid;
        /** 报名签到id */
        private Integer signId;

    }

}