package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityNameTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.activity.WorkInfoSyncQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameTimeChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 15:39:11
 */
@Slf4j
@Service
public class ActivityNameTimeChangeEventQueueService {

    @Resource
    private WorkInfoSyncQueue activityWorkInfoSyncQueueService;

    public void handle(ActivityNameTimeChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        activityWorkInfoSyncQueueService.push(eventOrigin.getActivityId());
    }

}
