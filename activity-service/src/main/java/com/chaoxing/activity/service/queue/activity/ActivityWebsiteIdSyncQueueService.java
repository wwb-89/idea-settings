package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动网站id同步队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityWebsiteIdSyncQueueService
 * @description
 * @blame wwb
 * @date 2021-05-10 10:09:04
 */
@Slf4j
@Service
public class ActivityWebsiteIdSyncQueueService implements IQueueService<Integer> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "website_id_sync";

    @Resource
    private RedissonClient redissonClient;

    public void add(Integer activityId) {
        push(redissonClient, CACHE_KEY, activityId);
    }

    public Integer get() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}