package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动作品征集信息同步队列服务
 * @author wwb
 * @version ver 1.0
 * @className WorkInfoSyncQueue
 * @description
 * @blame wwb
 * @date 2021-09-13 15:30:42
 */
@Slf4j
@Service
public class WorkInfoSyncQueue implements IQueue<Integer> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "work_info_sync";

    @Resource
    private RedissonClient redissonClient;

    public void push(Integer activityId) {
        push(redissonClient, KEY, activityId);
    }

    public Integer pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}