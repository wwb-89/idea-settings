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
 * @className ActivityStatSummaryQueueService
 * @description
 * @blame wwb
 * @date 2021-05-25 20:03:49
 */
@Slf4j
@Service
public class ActivityStatSummaryQueueService {

    /** 签到、签到率 */
    private static final String SIGN_IN_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_in";
    /** 合格数 */
    private static final String RESULT_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat_summary" + CacheConstant.CACHE_KEY_SEPARATOR + "result";

    @Resource
    private RedisTemplate redisTemplate;

    public void addSignInStat(Integer activityId) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(SIGN_IN_CACHE_KEY, activityId);
    }

    public Integer getSignInStat() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(SIGN_IN_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

    public void addResultStat(Integer activityId) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(RESULT_CACHE_KEY, activityId);
    }

    public Integer getResultStat() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(RESULT_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}
