package com.chaoxing.activity.task.stat;

import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueue;
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
    private ActivityStatSummaryQueue activityStatSummaryQueueService;

    @Resource
    private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;

    /**由于签到信息的改变引起的活动统计信息的修改处理
     * @Description 签到信息的改变包含：
     * 1、用户报名、取消报名
     * 2、用户签到、取消签到
     * 3、报名签到本身的修改（新增/修改报名或签到）
     * @author wwb
     * @Date 2021-06-22 11:14:32
     * @param 
     * @return void
    */
    @Scheduled(fixedDelay = 1L)
    public void handleSignStat() throws InterruptedException {
        Integer activityId = activityStatSummaryQueueService.pop();
        if (activityId == null) {
            return;
        }
        try {
            activityStatSummaryHandlerService.activityStatSummaryCalByActivity(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("活动 :{} 的活动统计汇总计算error:{}", activityId, e.getMessage());
            activityStatSummaryQueueService.push(activityId);
        }
    }

}
