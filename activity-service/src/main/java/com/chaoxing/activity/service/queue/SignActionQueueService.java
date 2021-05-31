package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
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
public class SignActionQueueService implements IQueueService<Integer> {

    /** 签到行为缓存key */
    private static final String SIGN_IN_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "sign_in_num_change_action";

    @Resource
    private RedissonClient redissonClient;

    public void addSignInNumChangeAction(Integer signId) {
        push(redissonClient, SIGN_IN_ACTION_CACHE_KEY, signId);
    }

    public Integer getSignInNumChangeAction() throws InterruptedException {
        return pop(redissonClient, SIGN_IN_ACTION_CACHE_KEY);
    }

}