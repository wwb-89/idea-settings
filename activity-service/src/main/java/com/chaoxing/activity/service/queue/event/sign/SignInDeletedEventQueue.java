package com.chaoxing.activity.service.queue.event.sign;

import com.chaoxing.activity.dto.event.sign.SignInDeletedEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**删除签到事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className SignInDeletedEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 19:09:11
 */
@Slf4j
@Service
public class SignInDeletedEventQueue implements IQueue<SignInDeletedEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_in_deleted";

    @Resource
    private RedissonClient redissonClient;

    public void push(SignInDeletedEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public SignInDeletedEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
