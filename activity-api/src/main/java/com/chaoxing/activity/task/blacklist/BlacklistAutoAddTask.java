package com.chaoxing.activity.task.blacklist;

import com.chaoxing.activity.service.blacklist.BlacklistHandleService;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueueService;
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
    private BlacklistAutoAddQueueService blacklistAutoAddQueueService;
    @Resource
    private BlacklistHandleService blacklistHandleService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        BlacklistAutoAddQueueService.QueueParamDTO queueParamDto = blacklistAutoAddQueueService.pop();
        if (queueParamDto == null) {
            return;
        }
        try {
            blacklistHandleService.activityEndHandleBlacklist(queueParamDto.getActivityId());
        } catch (Exception e) {
            e.printStackTrace();
            blacklistAutoAddQueueService.push(queueParamDto);
        }

    }

}