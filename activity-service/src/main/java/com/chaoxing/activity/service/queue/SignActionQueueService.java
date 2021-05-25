package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**报名签到行为队列服务
 * @author wwb
 * @version ver 1.0
 * @className SignActionQueueService
 * @description
 * @blame wwb
 * @date 2021-05-25 17:52:39
 */
@Slf4j
@Service
public class SignActionQueueService {

    /** 签到行为缓存key */
    private static final String SIGN_IN_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "sign_in_num_change_action";

    @Resource
    private RedisTemplate redisTemplate;

    public void addSignInNumChangeAction(Integer signId) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(SIGN_IN_ACTION_CACHE_KEY, signId);
    }

    public Integer getSignInNumChangeAction() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(SIGN_IN_ACTION_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}