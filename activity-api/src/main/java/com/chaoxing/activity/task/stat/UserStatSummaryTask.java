package com.chaoxing.activity.task.stat;

import com.chaoxing.activity.service.queue.UserStatSummaryQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryService;
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
    private UserStatSummaryService userStatSummaryService;

    @Scheduled(fixedDelay = 1L)
    public void handleUserSignInStat() {
        Integer uid = userStatSummaryQueueService.getUserSignInStat();
        if (uid == null) {
            return;
        }
        try {
            userStatSummaryService.updateUserSignInData(uid);
        } catch (Exception e) {
            userStatSummaryQueueService.addUserSignInStat(uid);
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void handleUserResultStat() {
        Integer uid = userStatSummaryQueueService.getUserResultStata();
        if (uid == null) {
            return;
        }
        try {
            userStatSummaryService.updateUserResultData(uid);
        } catch (Exception e) {
            userStatSummaryQueueService.addUserResultStat(uid);
        }
    }

}