package com.chaoxing.activity.service.queue.event.sign;

import com.chaoxing.activity.dto.event.sign.SignUpDeletedEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**报名删除事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className SignUpDeletedEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-27 19:08:50
 */
@Slf4j
@Service
public class SignUpDeletedEventQueue implements IQueue<SignUpDeletedEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "sign_up_deleted";

    @Resource
    private RedissonClient redissonClient;

    public void push(SignUpDeletedEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public SignUpDeletedEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }


}
