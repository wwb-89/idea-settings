package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动数据修改队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataChangeQueueService
 * @description
 * 给收藏活动和报名活动的用户发送活动信息修改的通知
 * @blame wwb
 * @date 2021-05-12 16:43:48
 */
@Slf4j
@Service
public class ActivityDataChangeQueueService implements IQueueService<Integer> {

    private static final String COLLECTED_NOTICE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_change_notice_collected";
    private static final String SIGNED_UP_NOTICE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_change_notice_signed_up";

    @Resource
    private RedissonClient redissonClient;

    public void add(Integer activityId) {
        addColleced(activityId);
        addSignedUp(activityId);
    }

    public void addColleced(Integer activityId) {
        push(redissonClient, COLLECTED_NOTICE_CACHE_KEY, activityId);
    }

    public Integer getCollected() throws InterruptedException {
        return pop(redissonClient, COLLECTED_NOTICE_CACHE_KEY);
    }

    public void addSignedUp(Integer activityId) {
        push(redissonClient, SIGNED_UP_NOTICE_CACHE_KEY, activityId);
    }

    public Integer getSignedUp() throws InterruptedException {
        return pop(redissonClient, SIGNED_UP_NOTICE_CACHE_KEY);
    }

}