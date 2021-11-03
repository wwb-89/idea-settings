package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**机构用户数据推送任务队列
 * @author wwb
 * @version ver 1.0
 * @className OrgUserDataPushQueue
 * @description
 * @blame wwb
 * @date 2021-11-02 16:56:43
 */
@Slf4j
@Service
public class OrgUserDataPushQueue implements IQueue<OrgUserDataPushQueue.QueueParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "org_user_data_push";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, CACHE_KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 用户id */
        private Integer uid;
        /** 活动id */
        private Integer activityId;

    }

}
