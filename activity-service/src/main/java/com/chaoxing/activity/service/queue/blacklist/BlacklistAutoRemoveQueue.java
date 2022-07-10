package com.chaoxing.activity.service.queue.blacklist;

import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**黑名单自动移除队列服务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoRemoveQueue
 * @description
 * @blame wwb
 * @date 2021-07-27 17:49:10
 */
@Slf4j
@Service
public class BlacklistAutoRemoveQueue implements IDelayedQueue<BlacklistAutoRemoveQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "blacklist_auto_remove";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParamDto) {
        LocalDateTime removeTime = queueParamDto.getRemoveTime();
        // 先删除
        remove(queueParamDto.getMarketId(), queueParamDto.getUid());
        push(redissonClient, KEY, queueParamDto, removeTime);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    public void remove(Integer marketId, Integer uid) {
        List<QueueParamDTO> queueParams = list(redissonClient, KEY);
        if (CollectionUtils.isEmpty(queueParams)) {
            return;
        }
        for (QueueParamDTO queueParam : queueParams) {
            if (Objects.equals(queueParam.getMarketId(), marketId) && Objects.equals(queueParam.getUid(), uid)) {
                remove(redissonClient, KEY, queueParam);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        private Integer marketId;
        private Integer uid;
        private LocalDateTime removeTime;

    }

}