package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**机构关联活动
 * @author wwb
 * @version ver 1.0
 * @className OrgAssociateActivityQueueService
 * @description 根据活动的市场（模版），关联结构创建相应模版的活动市场并关联这个活动
 * @blame wwb
 * @date 2021-09-14 15:08:52
 */
@Slf4j
@Service
public class OrgAssociateActivityQueueService implements IQueueService<OrgAssociateActivityQueueService.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "org_associate_activity";

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

        /** 活动id */
        private Integer activityId;
        /** 机构id */
        private Integer fid;
        /** uid */
        private Integer uid;

    }

}