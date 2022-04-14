package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
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
public class ActivityStatSummaryQueue implements IQueue<Integer> {

    /** 签到、签到率 */
    private static final String SIGN_IN_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat_summary";

    @Resource
    private RedissonClient redissonClient;

    public void push(Integer activityId) {
        push(redissonClient, SIGN_IN_CACHE_KEY, activityId);
    }

    public Integer pop() throws InterruptedException {
        return pop(redissonClient, SIGN_IN_CACHE_KEY);
    }

}
