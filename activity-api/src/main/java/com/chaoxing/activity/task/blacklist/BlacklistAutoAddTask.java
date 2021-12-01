package com.chaoxing.activity.task.blacklist;

import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueue;
import com.chaoxing.activity.service.queue.blacklist.handler.BlacklistAutoAddQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动结束自动添加黑名单任务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoAddTask
 * @description
 * @blame wwb
 * @date 2021-07-27 18:19:07
 */
@Component
public class BlacklistAutoAddTask {

    @Resource
    private BlacklistAutoAddQueue blacklistAutoAddQueue;
    @Resource
    private BlacklistAutoAddQueueService blacklistAutoAddQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        BlacklistAutoAddQueue.QueueParamDTO queueParamDto = blacklistAutoAddQueue.pop();
        if (queueParamDto == null) {
            return;
        }
        try {
            blacklistAutoAddQueueService.handle(queueParamDto.getActivityId());
        } catch (Exception e) {
            e.printStackTrace();
            blacklistAutoAddQueue.push(queueParamDto);
        }

    }

}