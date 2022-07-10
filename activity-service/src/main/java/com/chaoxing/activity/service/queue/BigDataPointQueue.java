package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**大数据积分推送
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointQueue
 * @description
 * @blame wwb
 * @date 2021-10-13 11:31:02
 */
@Slf4j
@Service
public class BigDataPointQueue implements IQueue<BigDataPointQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "big_data" + CacheConstant.CACHE_KEY_SEPARATOR + "point" + CacheConstant.CACHE_KEY_SEPARATOR;

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
        /** 机构id(活动机构id) */
        private Integer fid;
        /** 活动id */
        private Integer activityId;
        /** 积分类型 */
        private Integer pointType;

    }

}