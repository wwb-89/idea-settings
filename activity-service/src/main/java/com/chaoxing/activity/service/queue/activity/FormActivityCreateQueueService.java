package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.dto.manager.form.FormCreateActivity;
import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
public class FormActivityCreateQueueService implements IQueueService<FormCreateActivity> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "form_create_activity";

    @Resource
    private RedissonClient redissonClient;

    public void push(FormCreateActivity formCreateActivity) {
        push(redissonClient, CACHE_KEY, formCreateActivity);
    }

    public FormCreateActivity pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}