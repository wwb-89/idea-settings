package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserQualifiedEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户合格事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserQualifiedEventQueue
 * @description
 * @blame wwb
 * @date 2021-11-01 10:40:19
 */
@Slf4j
@Service
public class UserQualifiedEventQueue implements IQueue<UserQualifiedEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_qualified";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserQualifiedEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserQualifiedEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
