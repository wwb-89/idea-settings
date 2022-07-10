package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserStatSummaryChangeEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户活动统计改变事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryChangeEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-29 16:06:18
 */
@Slf4j
@Service
public class UserStatSummaryChangeEventQueue implements IQueue<UserStatSummaryChangeEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_stat_summary_change";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserStatSummaryChangeEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public UserStatSummaryChangeEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
