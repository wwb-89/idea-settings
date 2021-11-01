package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.ActivityDataPushQueue;
import com.chaoxing.activity.service.queue.activity.handler.ActivityDataPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动数据推送任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataPushTask
 * @description
 * @blame wwb
 * @date 2021-11-01 15:05:19
 */
@Slf4j
@Component
public class ActivityDataPushTask {

    @Resource
    private ActivityDataPushQueue activityDataPushQueue;
    @Resource
    private ActivityDataPushQueueService activityDataPushQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityDataPushQueue.QueueParamDTO queueParam = activityDataPushQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            activityDataPushQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动数据推送任务error:{}", queueParam, e.getMessage());
            e.printStackTrace();
            activityDataPushQueue.push(queueParam);
        }
    }

}