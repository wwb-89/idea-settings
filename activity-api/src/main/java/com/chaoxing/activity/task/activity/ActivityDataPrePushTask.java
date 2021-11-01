package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.ActivityDataPrePushQueue;
import com.chaoxing.activity.service.queue.activity.handler.ActivityDataPrePushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动数据预推送任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataPrePushTask
 * @description 触发活动推送，将分发到活动配置的所有需要推送的目的地队列任务
 * @blame wwb
 * @date 2021-11-01 15:02:31
 */
@Slf4j
@Component
public class ActivityDataPrePushTask {

    @Resource
    private ActivityDataPrePushQueue activityDataPrePushQueue;
    @Resource
    private ActivityDataPrePushQueueService activityDataPrePushQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        Integer activityId = activityDataPrePushQueue.pop();
        if (activityId == null) {
            return;
        }
        try {
            activityDataPrePushQueueService.handle(activityId);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动数据预推送任务error:{}", activityId, e.getMessage());
            e.printStackTrace();
            activityDataPrePushQueue.push(activityId);
        }
    }

}