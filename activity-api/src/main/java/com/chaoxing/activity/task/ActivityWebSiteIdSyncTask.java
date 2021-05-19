package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.queue.ActivityWebsiteIdSyncQueueService;
import com.chaoxing.activity.util.exception.BusinessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityWebSiteIdSyncTask
 * @description
 * @blame wwb
 * @date 2021-05-10 15:15:00
 */
@Component
public class ActivityWebSiteIdSyncTask {

    @Resource
    private ActivityWebsiteIdSyncQueueService activityWebsiteIdSyncQueueService;
    @Resource
    private ActivityHandleService activityHandleService;

    /**同步活动websiteId
     * @Description 
     * @author wwb
     * @Date 2021-05-10 15:28:20
     * @param 
     * @return void
    */
    @Scheduled(fixedDelay = 1000L)
    public void sync() {
        Integer activityId = activityWebsiteIdSyncQueueService.get();
        if (activityId == null) {
            return;
        }
        try {
            activityHandleService.syncActivityWebsiteId(activityId);
        } catch (BusinessException e) {
            activityWebsiteIdSyncQueueService.add(activityId);
        }
    }

}