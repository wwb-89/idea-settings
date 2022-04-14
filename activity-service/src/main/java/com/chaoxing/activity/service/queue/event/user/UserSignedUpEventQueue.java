package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserSignedUpEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户成功报名事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserSignedUpEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 18:20:37
 */
@Slf4j
@Service
public class UserSignedUpEventQueue implements IQueue<UserSignedUpEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_signed_up";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserSignedUpEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserSignedUpEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
