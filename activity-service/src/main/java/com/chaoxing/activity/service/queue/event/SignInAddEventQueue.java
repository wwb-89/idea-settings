package com.chaoxing.activity.service.queue.event;

import com.chaoxing.activity.dto.event.SignInAddEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignInAddEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 19:08:59
 */
@Slf4j
@Service
public class SignInAddEventQueue implements IQueue<SignInAddEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_in_add";

    @Resource
    private RedissonClient redissonClient;

    public void push(SignInAddEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public SignInAddEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}