package com.chaoxing.activity.task.blacklist;

import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoRemoveQueue;
import com.chaoxing.activity.service.queue.blacklist.handler.BlacklistAutoRemoveQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoRemoveTask
 * @description
 * @blame wwb
 * @date 2021-07-27 18:03:20
 */
@Component
public class BlacklistAutoRemoveTask {

    @Resource
    private BlacklistAutoRemoveQueue blacklistAutoRemoveQueue;
    @Resource
    private BlacklistAutoRemoveQueueService blacklistAutoRemoveQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        BlacklistAutoRemoveQueue.QueueParamDTO queueParamDto = blacklistAutoRemoveQueue.pop();
        if (queueParamDto == null) {
            return;
        }
        try {
            blacklistAutoRemoveQueueService.handle(queueParamDto.getMarketId(), queueParamDto.getUid());
        } catch (Exception e) {
            e.printStackTrace();
            blacklistAutoRemoveQueue.push(queueParamDto);
        }
    }

}
