package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户行为记录队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordQueueService
 * @description 新增用户行为（报名签到删除导致的删除行为直接删除（批量）记录，不在此处处理）
 * @blame wwb
 * @date 2021-06-23 17:47:54
 */
@Slf4j
@Service
public class UserActionRecordQueueService implements IQueueService<UserActionQueueService.QueueParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_action_record";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserActionQueueService.QueueParamDTO queueParam) {
        push(redissonClient, CACHE_KEY, queueParam);
    }

    public UserActionQueueService.QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}