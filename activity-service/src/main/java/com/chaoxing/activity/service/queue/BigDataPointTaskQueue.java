package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**大数据积分任务队列服务
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointTaskQueue
 * @description
 * 活动进行或结束后触发任务
 * @blame wwb
 * @date 2021-10-13 11:23:13
 */
@Slf4j
@Service
public class BigDataPointTaskQueue implements IQueue<BigDataPointTaskQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "big_data" + CacheConstant.CACHE_KEY_SEPARATOR + "point" + CacheConstant.CACHE_KEY_SEPARATOR + "task";

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

        /** 活动id */
        private Integer activityId;
        /** 创建机构id */
        private Integer createFid;
        /** 是否新增(活动结束就新增、开始就删除) */
        private Boolean add;

    }

}