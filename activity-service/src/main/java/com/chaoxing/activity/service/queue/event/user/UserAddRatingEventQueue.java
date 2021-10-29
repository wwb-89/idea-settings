package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserAddRatingEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户新增评价事件队列
 * @author wwb
 * @version ver 1.0
 * @className UserAddRatingEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 17:48:20
 */
@Slf4j
@Service
public class UserAddRatingEventQueue implements IQueue<UserAddRatingEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_add_rating";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserAddRatingEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserAddRatingEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}