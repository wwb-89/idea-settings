package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserCancelSignUpEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户取消报名事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignUpEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 17:59:29
 */
@Slf4j
@Service
public class UserCancelSignUpEventQueue implements IQueue<UserCancelSignUpEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_cancel_sign_up";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserCancelSignUpEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserCancelSignUpEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
