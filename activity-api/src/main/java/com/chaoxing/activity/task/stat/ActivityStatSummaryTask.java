package com.chaoxing.activity.task.stat;

import com.chaoxing.activity.service.queue.ActivityStatSummaryQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatSummaryTask
 * @description
 * @blame wwb
 * @date 2021-05-26 09:59:41
 */
@Slf4j
@Component
public class ActivityStatSummaryTask {

    @Resource
    private ActivityStatSummaryQueueService activityStatSummaryQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handleSIgnInStat() {
        Integer activityId = activityStatSummaryQueueService.getSignInStat();
        if (activityId == null) {
            return;
        }
        // TODO
    }

    @Scheduled(fixedDelay = 1L)
    public void handleResultStat() {
        Integer activityId = activityStatSummaryQueueService.getResultStat();
        if (activityId == null) {
            return;
        }
        // TODO
    }

}
