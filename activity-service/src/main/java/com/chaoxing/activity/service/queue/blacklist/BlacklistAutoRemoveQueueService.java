package com.chaoxing.activity.service.queue.blacklist;

import com.chaoxing.activity.service.queue.IDelayedQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**黑名单自动移除队列服务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoRemoveQueueService
 * @description
 * @blame wwb
 * @date 2021-07-27 17:49:10
 */
@Slf4j
@Service
public class BlacklistAutoRemoveQueueService implements IDelayedQueueService<BlacklistAutoRemoveQueueService.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "blacklist_auto_remove";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParamDto) {
        LocalDateTime removeTime = queueParamDto.getRemoveTime();
        queueParamDto.setRemoveTime(null);
        // 先删除
        remove(queueParamDto.getMarketId(), queueParamDto.getUid());
        push(redissonClient, KEY, queueParamDto, removeTime);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    public void remove(Integer marketId, Integer uid) {
        remove(redissonClient, KEY, new QueueParamDTO(marketId, uid, null));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class QueueParamDTO {

        @EqualsAndHashCode.Include
        private Integer marketId;
        @EqualsAndHashCode.Include
        private Integer uid;
        private LocalDateTime removeTime;

    }

}