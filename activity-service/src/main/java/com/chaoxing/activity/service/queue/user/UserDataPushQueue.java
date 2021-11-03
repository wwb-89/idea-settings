package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;

/**用户数据推送队列
 * @author wwb
 * @version ver 1.0
 * @className UserDataPushQueue
 * @description
 * @blame wwb
 * @date 2021-11-02 15:35:34
 */
public class UserDataPushQueue implements IQueue<UserDataPushQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_data_push";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 用户id */
        private Integer uid;
        /** 活动id */
        private Integer activityId;
        /** 数据推送配置id */
        private Integer dataPushConfigId;

    }

}
