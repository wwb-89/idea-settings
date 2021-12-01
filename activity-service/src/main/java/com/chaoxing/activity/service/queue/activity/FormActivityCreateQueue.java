package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateActivity;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**通过表单创建活动队列服务
 * @author wwb
 * @version ver 1.0
 * @className FormActivityCreateQueueService
 * @description
 * @blame wwb
 * @date 2021-05-11 16:18:35
 */
@Slf4j
@Service
public class FormActivityCreateQueue implements IQueue<WfwFormCreateActivity> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "form_create_activity";

    @Resource
    private RedissonClient redissonClient;

    public void push(WfwFormCreateActivity formCreateActivity) {
        push(redissonClient, CACHE_KEY, formCreateActivity);
    }

    public void delayPush(WfwFormCreateActivity formCreateActivity) {
        delayPush(redissonClient, CACHE_KEY, formCreateActivity);
    }

    public WfwFormCreateActivity pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}