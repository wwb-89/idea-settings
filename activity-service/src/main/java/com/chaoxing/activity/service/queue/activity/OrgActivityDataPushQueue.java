package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**机构活动数据推送队列
 * @author wwb
 * @version ver 1.0
 * @className DataPushQueue
 * @description 旧版本基于机构配置的数据推送
 * @blame wwb
 * @date 2021-06-24 19:43:42
 */
@Slf4j
@Service
public class OrgActivityDataPushQueue implements IQueue<Integer> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "org_activity_data_push";

    @Resource
    private RedissonClient redissonClient;

    public void push(Integer activityId) {
        push(redissonClient, CACHE_KEY, activityId);
    }

    public void delayPush(Integer activityId) {
        delayPush(redissonClient, CACHE_KEY, activityId);
    }

    public Integer pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}