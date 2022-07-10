package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**自定义应用接口调用队列
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/15 2:11 PM
 * @version: 1.0
 */
@Slf4j
@Service
public class CustomAppInterfaceCallQueue implements IQueue<CustomAppInterfaceCallQueue.QueueParamDTO> {


    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "custom_interface_call";

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
        /** 活动id */
        private Integer activityId;
        /** 用户fid */
        private Integer fid;
        /** 接口调用id */
        private Integer interfaceCallId;
    }

}
