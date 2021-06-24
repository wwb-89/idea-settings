package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**用户行为记录有效性更新服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordValidQueueService
 * @description 批量更新用户的行为记录有效性，影响的因素有：
 * 1、新增/删除报名
 * 2、新增/删除签到
 * @blame wwb
 * @date 2021-06-24 10:56:45
 */
@Slf4j
@Service
public class UserActionRecordValidQueueService implements IQueueService<UserActionRecordValidQueueService.QueueParamDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_action_record_valid";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, CACHE_KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

    @Data
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 活动id */
        private Integer activityId;
        /** 主键 */
        private String identify;
        /**有效的 */
        private Boolean valid;
        /** 时间 */
        private LocalDateTime time;

    }

}