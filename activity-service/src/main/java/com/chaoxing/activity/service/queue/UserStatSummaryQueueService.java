package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
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
public class UserStatSummaryQueueService {

    /** 用户签到队列缓存key */
    private static final String USER_SIGN_IN_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_in";
    /** 用户成绩队列缓存key */
    private static final String USER_RESULT_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "result";

    @Resource
    private RedisTemplate redisTemplate;

    public void addUserSignInStat(Integer uid) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(USER_SIGN_IN_CACHE_KEY, uid);
    }

    public Integer getUserSignInStat() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(USER_SIGN_IN_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

    public void addUserResultStat(Integer uid) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(USER_RESULT_CACHE_KEY, uid);
    }

    public Integer getUserResultStata() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(USER_RESULT_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}