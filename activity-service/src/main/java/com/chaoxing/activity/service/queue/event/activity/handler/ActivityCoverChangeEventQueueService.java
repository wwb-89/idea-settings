package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityCoverChangeEventOrigin;
import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-26 18:20:56
 */
@Slf4j
@Service
public class ActivityCoverChangeEventQueueService {

    @Resource
    private ActivityCoverUrlSyncService activityCoverUrlSyncService;

    public void handle(ActivityCoverChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        activityCoverUrlSyncService.updateActivityCoverUrl(activityId);
    }

}
