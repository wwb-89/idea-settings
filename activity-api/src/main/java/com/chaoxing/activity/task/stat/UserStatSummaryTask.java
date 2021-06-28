package com.chaoxing.activity.task.stat;

import com.chaoxing.activity.service.queue.user.UserStatSummaryQueueService;
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
    private UserStatSummaryQueueService userStatSummaryQueueService;
    @Resource
    private UserStatSummaryHandleService userStatSummaryService;

    @Scheduled(fixedDelay = 1L)
    public void handleUserSignInStat() throws InterruptedException {
        UserStatSummaryQueueService.QueueParamDTO queueParam = userStatSummaryQueueService.popUserSignStat();
        if (queueParam == null) {
            return;
        }
        try {
            userStatSummaryService.updateUserSignData(queueParam.getUid(), queueParam.getActivityId());
        } catch (Exception e) {
            userStatSummaryQueueService.pushUserSignStat(queueParam);
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void handleUserResultStat() throws InterruptedException {
        UserStatSummaryQueueService.QueueParamDTO queueParam = userStatSummaryQueueService.popUserResultStata();
        if (queueParam == null) {
            return;
        }
        try {
            userStatSummaryService.updateUserResult(queueParam.getUid(), queueParam.getActivityId());
        } catch (Exception e) {
            userStatSummaryQueueService.pushUserResultStat(queueParam);
        }
    }

}