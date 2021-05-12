package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.dto.manager.form.FormCreateActivity;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
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
public class FormActivityCreateQueueService {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "form_create_activity";

    @Resource
    private RedisTemplate redisTemplate;

    public void add(FormCreateActivity formCreateActivity) {
        ListOperations<String, FormCreateActivity> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(CACHE_KEY, formCreateActivity);
    }

    public FormCreateActivity get() {
        ListOperations<String, FormCreateActivity> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}