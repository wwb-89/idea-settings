package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/1 11:17 上午
 * <p>
 */
@Slf4j
@Service
public class ExportQueue implements IQueue<Integer> {

    /** 记录导出 */
    private static final String EXPORT_RECORD_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "export_record";

    @Resource
    private RedissonClient redissonClient;

    public void push(Integer taskId) {
        push(redissonClient, EXPORT_RECORD_CACHE_KEY, taskId);
    }

    public Integer pop() throws InterruptedException {
        return pop(redissonClient, EXPORT_RECORD_CACHE_KEY);
    }
}
