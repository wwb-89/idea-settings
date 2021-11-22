package com.chaoxing.activity.service.queue.activity;

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

/**万能表单关联活动数据更新队列
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityDataUpdateQueue
 * @description
 * @blame wwb
 * @date 2021-11-22 18:00:59
 */
@Slf4j
@Service
public class WfwFormActivityDataUpdateQueue implements IQueue<WfwFormActivityDataUpdateQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "wfw_form_activity_data_update";

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
        private Integer fid;
        private Integer formId;
        private Integer formUserId;

    }

}