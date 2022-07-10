package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserDeleteRatingEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户删除评价事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserDeleteRatingEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-28 18:05:19
 */
@Slf4j
@Service
public class UserDeleteRatingEventQueue implements IQueue<UserDeleteRatingEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_delete_rating";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserDeleteRatingEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserDeleteRatingEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
