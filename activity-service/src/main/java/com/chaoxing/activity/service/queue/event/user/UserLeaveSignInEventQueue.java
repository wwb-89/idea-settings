package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserLeaveSignInEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户签到请假事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserLeaveSignInEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 18:11:24
 */
@Slf4j
@Service
public class UserLeaveSignInEventQueue implements IQueue<UserLeaveSignInEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_leave_sign_in";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserLeaveSignInEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserLeaveSignInEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
