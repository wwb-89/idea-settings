package com.chaoxing.activity.service.queue.notice;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**黑名单通知
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/6 4:40 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class BlacklistUserNoticeQueue implements IQueue<BlacklistUserNoticeQueue.QueueParamDTO> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice" + CacheConstant.CACHE_KEY_SEPARATOR + "blacklist_user";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 收件用户id */
        private List<Integer> uids;
        /** 通知标题 */
        private String title;
        /** 通知内容 */
        private String content;
        /** 附件 */
        private String attachment;

    }
}
