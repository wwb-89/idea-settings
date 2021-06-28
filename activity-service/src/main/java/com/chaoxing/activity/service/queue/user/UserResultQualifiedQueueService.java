package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户成绩合格队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultChangeNoticeQueueService
 * @description 用户成绩合格判定后需要通知报名签到（以便更新表单中用户的活动记录）
 * @blame wwb
 * @date 2021-06-25 10:05:38
 */
@Slf4j
@Service
public class UserResultQualifiedQueueService implements IQueueService<UserResultQualifiedQueueService.QueueParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_result_qualified";

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
        /** 报名签到id */
        private Integer signId;

    }
}