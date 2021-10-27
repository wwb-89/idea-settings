package com.chaoxing.activity.service.queue.blacklist;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**黑名单自动添加队列服务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoAddQueue
 * @description
 * @blame wwb
 * @date 2021-07-27 18:14:01
 */
@Slf4j
@Service
public class BlacklistAutoAddQueue implements IQueue<BlacklistAutoAddQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "blacklist_auto_add";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParamDto) {
        push(redissonClient, KEY, queueParamDto);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        private Integer activityId;

    }

}