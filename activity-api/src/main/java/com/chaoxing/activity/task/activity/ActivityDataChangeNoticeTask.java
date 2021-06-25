package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.notice.ActivityDataChangeNoticeService;
import com.chaoxing.activity.service.queue.activity.ActivityDataChangeQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动数据修改通知任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataChangeNoticeTask
 * @description
 * @blame wwb
 * @date 2021-05-12 17:11:09
 */
@Component
public class ActivityDataChangeNoticeTask {

    @Resource
    private ActivityDataChangeQueueService activityDataChangeQueueService;
    @Resource
    private ActivityDataChangeNoticeService activityDataChangeNoticeService;

    @Scheduled(fixedDelay = 1L)
    public void handleCollectedNotice() throws InterruptedException {
        Integer activityId = activityDataChangeQueueService.getCollected();
        if (activityId == null) {
            return;
        }
        try {
            activityDataChangeNoticeService.sendToCollected(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            activityDataChangeQueueService.addColleced(activityId);
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void handleSignedUpNotice() throws InterruptedException {
        Integer activityId = activityDataChangeQueueService.getSignedUp();
        if (activityId == null) {
            return;
        }
        try {
            activityDataChangeNoticeService.sendToSignedUp(activityId);
        } catch (Exception e) {
            activityDataChangeQueueService.addSignedUp(activityId);
        }
    }

}