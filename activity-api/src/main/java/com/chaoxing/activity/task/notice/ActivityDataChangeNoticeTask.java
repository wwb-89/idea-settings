package com.chaoxing.activity.task.notice;

import com.chaoxing.activity.service.queue.notice.ActivityDataChangeNoticeQueue;
import com.chaoxing.activity.service.queue.notice.handler.ActivityDataChangeNoticeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataChangeNoticeTask
 * @description
 * @blame wwb
 * @date 2021-10-26 17:56:06
 */
@Slf4j
@Component
public class ActivityDataChangeNoticeTask {

    @Resource
    private ActivityDataChangeNoticeQueue activityDataChangeNoticeQueue;
    @Resource
    private ActivityDataChangeNoticeQueueService activityDataChangeNoticeQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        Integer activityId = activityDataChangeNoticeQueue.pop();
        if (activityId == null) {
            return;
        }
        try {
            activityDataChangeNoticeQueueService.handle(activityId);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动信息改变发送通知error:{}", activityId, e.getMessage());
            e.printStackTrace();
            activityDataChangeNoticeQueue.push(activityId);
        }
    }

}
