package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityStartTimeReachEventOrigin;
import com.chaoxing.activity.service.activity.ActivityStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartTimeReachEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 11:22:56
 */
@Slf4j
@Service
public class ActivityStartTimeReachEventQueueService {

    @Resource
    private ActivityStatusService activityStatusService;

    public void handle(ActivityStartTimeReachEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        activityStatusService.statusUpdate(activityId);
    }

}