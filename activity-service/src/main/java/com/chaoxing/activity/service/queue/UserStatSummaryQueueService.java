package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
public class UserStatSummaryQueueService implements IQueueService<Integer> {

    /** 用户签到队列缓存key */
    private static final String USER_SIGN_IN_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_in";
    /** 用户成绩队列缓存key */
    private static final String USER_RESULT_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "result";

    @Resource
    private RedissonClient redissonClient;

    public void addUserSignInStat(Integer uid) {
        push(redissonClient, USER_SIGN_IN_CACHE_KEY, uid);
    }

    public Integer getUserSignInStat() throws InterruptedException {
        return pop(redissonClient, USER_SIGN_IN_CACHE_KEY);
    }

    public void addUserResultStat(Integer uid) {
        push(redissonClient, USER_RESULT_CACHE_KEY, uid);
    }

    public Integer getUserResultStata() throws InterruptedException {
        return pop(redissonClient, USER_RESULT_CACHE_KEY);
    }

}