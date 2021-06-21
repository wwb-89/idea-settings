package com.chaoxing.activity.task.stat;

import com.chaoxing.activity.service.activity.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
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

    @Resource
    private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;

    @Scheduled(fixedDelay = 1L)
    public void handleSignInStat() throws InterruptedException {
        Integer activityId = activityStatSummaryQueueService.getSignInStat();
        if (activityId == null) {
            return;
        }
        try {
            activityStatSummaryHandlerService.activityStatSummaryCalByActivity(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("活动 :{} 的活动统计汇总计算error:{}", activityId, e.getMessage());
            activityStatSummaryQueueService.addSignInStat(activityId);
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void handleResultStat() throws InterruptedException {
        Integer activityId = activityStatSummaryQueueService.getResultStat();
        if (activityId == null) {
            return;
        }
        try {
            activityStatSummaryHandlerService.activityStatSummaryCalByActivity(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("活动 :{} 的活动统计汇总计算error:{}", activityId, e.getMessage());
            activityStatSummaryQueueService.addResultStat(activityId);
        }
    }

}
