package com.chaoxing.activity.task.notice;

import com.chaoxing.activity.service.activity.manager.ActivityPushReminderService;
import com.chaoxing.activity.service.queue.notice.ActivityReminderNoticeQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动提醒推送任务
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/11 6:33 PM
 * @version: 1.0
 */
@Slf4j
@Component
public class ActivityReminderNoticeTask {

    @Resource
    private ActivityPushReminderService activityPushReminderService;
    @Resource
    private ActivityReminderNoticeQueue activityReminderNoticeQueue;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        ActivityReminderNoticeQueue.QueueParamDTO queueParam = activityReminderNoticeQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            activityPushReminderService.sendNotice(queueParam.getActivityId());
        } catch (Exception e) {
            log.error("给活动:{}, 发送通知提醒error:{}", queueParam.getActivityId(), e.getMessage());
            e.printStackTrace();
            activityReminderNoticeQueue.push(queueParam);
        }
    }


}
