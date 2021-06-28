package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.data.DataPushService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**数据推送队列
 * @author wwb
 * @version ver 1.0
 * @className DataPushQueueService
 * @description
 * @blame wwb
 * @date 2021-06-24 19:43:42
 */
@Slf4j
@Service
public class DataPushQueueService implements IQueueService<DataPushService.DataPushParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "data_push";

    @Resource
    private RedissonClient redissonClient;

    public void push(DataPushService.DataPushParamDTO queueParam) {
        push(redissonClient, CACHE_KEY, queueParam);
    }

    public DataPushService.DataPushParamDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}