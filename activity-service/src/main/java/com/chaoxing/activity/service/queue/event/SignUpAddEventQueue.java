package com.chaoxing.activity.service.queue.event;

import com.chaoxing.activity.dto.event.SignUpAddEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignUpAddEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 19:08:38
 */
@Slf4j
@Service
public class SignUpAddEventQueue implements IQueue<SignUpAddEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_up_add";

    @Resource
    private RedissonClient redissonClient;

    public void push(SignUpAddEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public SignUpAddEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
