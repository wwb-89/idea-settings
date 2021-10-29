package com.chaoxing.activity.task.notice;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.notice.ActivityCollectedUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.handler.ActivityCollectedUserNoticeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**给收藏活动的用户发送通知
 * @author wwb
 * @version ver 1.0
 * @className ActivityCollectedUserNoticeTask
 * @description
 * @blame wwb
 * @date 2021-10-29 15:14:35
 */
@Slf4j
@Component
public class ActivityCollectedUserNoticeTask {

    @Resource
    private ActivityCollectedUserNoticeQueue activityCollectedUserNoticeQueue;
    @Resource
    private ActivityCollectedUserNoticeQueueService activityCollectedUserNoticeQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam = activityCollectedUserNoticeQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            activityCollectedUserNoticeQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 给收藏活动的用户发送通知error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            activityCollectedUserNoticeQueue.push(queueParam);
        }
    }

}
