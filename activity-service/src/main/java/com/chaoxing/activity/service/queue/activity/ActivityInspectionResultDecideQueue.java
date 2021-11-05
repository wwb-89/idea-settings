package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动考核结果判定服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityInspectionResultDecideQueueService
 * @description 当活动的考核设置配置了自动考核且活动结束的时候需要自动判定用户考核结果
 * @blame wwb
 * @date 2021-06-25 15:33:28
 */
@Slf4j
@Service
public class ActivityInspectionResultDecideQueue implements IQueue<Integer> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_inspection_result_decide";

    @Resource
    private RedissonClient redissonClient;

    public void push(Integer activityId) {
        push(redissonClient, CACHE_KEY, activityId);
    }

    public Integer pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}