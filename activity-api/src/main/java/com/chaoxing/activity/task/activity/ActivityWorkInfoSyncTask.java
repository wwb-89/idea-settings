package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.queue.activity.ActivityWorkInfoSyncQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityWorkInfoSyncTask
 * @description
 * @blame wwb
 * @date 2021-09-13 15:38:03
 */
@Slf4j
@Component
public class ActivityWorkInfoSyncTask {

    @Resource
    private ActivityWorkInfoSyncQueueService activityWorkInfoSyncQueueService;
    @Resource
    private ActivityHandleService activityHandleService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        Integer activityId = activityWorkInfoSyncQueueService.pop();
        try {
            activityHandleService.updateWorkInfo(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            activityWorkInfoSyncQueueService.push(activityId);
        }
    }

}
