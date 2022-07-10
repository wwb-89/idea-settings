package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserUnQualifiedEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户不合格事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserUnQualifiedEventQueue
 * @description
 * @blame wwb
 * @date 2021-11-01 10:46:07
 */
@Slf4j
@Service
public class UserUnQualifiedEventQueue implements IQueue<UserUnQualifiedEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_unqualified";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserUnQualifiedEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserUnQualifiedEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}