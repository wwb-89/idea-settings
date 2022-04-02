package com.chaoxing.activity.task.notice;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.notice.ActivitySignedUpUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.handler.ActivitySignedUpUserNoticeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**给活动报名的用户发送通知
 * @author wwb
 * @version ver 1.0
 * @className ActivitySignedUpUserNoticeTask
 * @description
 * @blame wwb
 * @date 2021-10-26 15:17:55
 */
@Slf4j
@Component
public class ActivitySignedUpUserNoticeTask {

    @Resource
    private ActivitySignedUpUserNoticeQueue activitySignedUpUserNoticeQueue;
    @Resource
    private ActivitySignedUpUserNoticeQueueService activitySignedUpUserNoticeQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        ActivitySignedUpUserNoticeQueue.QueueParamDTO queueParam = activitySignedUpUserNoticeQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            activitySignedUpUserNoticeQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 给报名活动的用户发送通知error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            activitySignedUpUserNoticeQueue.push(queueParam);
        }
    }

}
