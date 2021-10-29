package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserActivityStatChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户活动统计改变事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserActivityStatChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-29 16:06:18
 */
@Slf4j
@Service
public class UserActivityStatChangeEventQueue implements IQueue<UserActivityStatChangeEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_activity_stat_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserActivityStatChangeEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserActivityStatChangeEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
