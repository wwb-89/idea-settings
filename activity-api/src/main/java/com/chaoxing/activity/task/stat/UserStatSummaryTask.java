package com.chaoxing.activity.task.stat;

import com.chaoxing.activity.service.queue.user.UserStatSummaryQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryTask
 * @description
 * @blame wwb
 * @date 2021-05-26 10:01:26
 */
@Slf4j
@Component
public class UserStatSummaryTask {

    @Resource
    private UserStatSummaryQueue userStatSummaryQueueService;
    @Resource
    private UserStatSummaryHandleService userStatSummaryService;

    @Scheduled(fixedDelay = 1L)
    public void handleUserSignInStat() throws InterruptedException {
        UserStatSummaryQueue.QueueParamDTO queueParam = userStatSummaryQueueService.popUserSignStat();
        if (queueParam == null) {
            return;
        }
        try {
            userStatSummaryService.updateUserSignData(queueParam.getUid(), queueParam.getActivityId());
        } catch (Exception e) {
            e.printStackTrace();
            userStatSummaryQueueService.pushUserSignStat(queueParam);
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void handleUserResultStat() throws InterruptedException {
        UserStatSummaryQueue.QueueParamDTO queueParam = userStatSummaryQueueService.popUserResultStata();
        if (queueParam == null) {
            return;
        }
        try {
            userStatSummaryService.updateUserResult(queueParam.getUid(), queueParam.getActivityId());
        } catch (Exception e) {
            e.printStackTrace();
            userStatSummaryQueueService.pushUserResultStat(queueParam);
        }
    }

}