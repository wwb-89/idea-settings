package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.OrgActivityDataPushQueue;
import com.chaoxing.activity.service.queue.activity.handler.OrgActivityDataPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**机构活动数据推送任务
 * @author wwb
 * @version ver 1.0
 * @className OrgActivityDataPushTask
 * @description
 * @blame wwb
 * @date 2021-06-24 19:48:47
 */
@Slf4j
@Component
public class OrgActivityDataPushTask {

    @Resource
    private OrgActivityDataPushQueue orgActivityDataPushQueue;
    @Resource
    private OrgActivityDataPushQueueService orgActivityDataPushQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        Integer activityId = orgActivityDataPushQueue.pop();
        if (activityId == null) {
            return;
        }
        try {
            orgActivityDataPushQueueService.handle(activityId);
        } catch (Exception e) {
            log.error("根据参数:{} 处理机构活动数据推送任务error:{}", activityId, e.getMessage());
            e.printStackTrace();
            orgActivityDataPushQueue.delayPush(activityId);
        }
    }

}
