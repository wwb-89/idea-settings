package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.service.data.DataPushService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**数据推送队列
 * @author wwb
 * @version ver 1.0
 * @className DataPushQueue
 * @description 旧版本基于机构配置的数据推送
 * @blame wwb
 * @date 2021-06-24 19:43:42
 */
@Slf4j
@Service
public class DataPushQueue implements IQueue<DataPushService.DataPushParamDTO> {

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