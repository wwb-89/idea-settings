package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
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
public class ActivityWebsiteIdSyncQueueService {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "website_id_sync";

    @Resource
    private RedisTemplate redisTemplate;

    public void add(Integer activityId) {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(CACHE_KEY, activityId);
    }

    public Integer get() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}