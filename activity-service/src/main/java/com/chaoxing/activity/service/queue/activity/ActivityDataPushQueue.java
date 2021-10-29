package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.DataPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动数据推送队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataPushQueue
 * @description
 * @blame wwb
 * @date 2021-10-29 16:59:11
 */
@Slf4j
@Service
public class ActivityDataPushQueue implements IQueue<ActivityDataPushQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_data_pre_push";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        private Integer activityId;
        private Integer dataPushConfigId;
        private DataPushTypeEnum dataPushType;

    }

}
