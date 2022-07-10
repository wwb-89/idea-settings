package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserSignedInEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户签到成功时间队列
 * @author wwb
 * @version ver 1.0
 * @className UserSignedInEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 10:43:02
 */
@Slf4j
@Service
public class UserSignedInEventQueue implements IQueue<UserSignedInEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_signed_in";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserSignedInEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserSignedInEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}