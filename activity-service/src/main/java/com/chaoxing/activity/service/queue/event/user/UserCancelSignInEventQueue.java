package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserCancelSignInEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户取消签到事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignInEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 17:51:44
 */
@Slf4j
@Service
public class UserCancelSignInEventQueue implements IQueue<UserCancelSignInEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_cancel_sign_in";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserCancelSignInEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserCancelSignInEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
