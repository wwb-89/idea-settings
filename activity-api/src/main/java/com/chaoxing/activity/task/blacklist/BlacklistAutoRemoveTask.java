package com.chaoxing.activity.task.blacklist;

import com.chaoxing.activity.service.blacklist.BlacklistHandleService;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoRemoveQueueService;
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
    private BlacklistAutoRemoveQueueService blacklistAutoRemoveQueueService;
    @Resource
    private BlacklistHandleService blacklistHandleService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        BlacklistAutoRemoveQueueService.QueueParamDTO queueParamDto = blacklistAutoRemoveQueueService.pop();
        if (queueParamDto == null) {
            return;
        }
        try {
            blacklistHandleService.autoRemoveBlacklist(queueParamDto.getMarketId(), queueParamDto.getUid());
        } catch (Exception e) {
            e.printStackTrace();
            blacklistAutoRemoveQueueService.push(queueParamDto);
        }
    }

}
