package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityEndTimeReachEventOrigin;
import com.chaoxing.activity.service.activity.ActivityStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndTimeReachEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 11:25:04
 */
@Slf4j
@Service
public class ActivityEndTimeReachEventQueueService {

    @Resource
    private ActivityStatusService activityStatusService;

    public void handle(ActivityEndTimeReachEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        activityStatusService.statusUpdate(eventOrigin.getActivityId());
    }

}
