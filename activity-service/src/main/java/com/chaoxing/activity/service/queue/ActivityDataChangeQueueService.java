package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
public class ActivityDataChangeQueueService {

    private static final String COLLECTED_NOTICE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_change_notice_collected";
    private static final String SIGNED_UP_NOTICE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_change_notice_signed_up";

    @Resource
    private RedisTemplate redisTemplate;

    public void add(Integer activityId) {
        addColleced(activityId);
        addSignedUp(activityId);
    }

    public void addColleced(Integer activityId) {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(COLLECTED_NOTICE_CACHE_KEY, activityId);
    }

    public void addSignedUp(Integer activityId) {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(SIGNED_UP_NOTICE_CACHE_KEY, activityId);
    }

    public Integer getCollected() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(COLLECTED_NOTICE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

    public Integer getSignedUp() {
        ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(SIGNED_UP_NOTICE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}