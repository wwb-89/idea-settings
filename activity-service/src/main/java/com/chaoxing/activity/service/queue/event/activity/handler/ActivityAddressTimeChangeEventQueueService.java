package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityAddressTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.notice.ActivityDataChangeNoticeQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityAddressTimeChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 15:47:46
 */
@Slf4j
@Service
public class ActivityAddressTimeChangeEventQueueService {

    @Resource
    private ActivityDataChangeNoticeQueue activityDataChangeNoticeQueue;

    public void handle(ActivityAddressTimeChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        activityDataChangeNoticeQueue.push(eventOrigin.getActivityId());
    }

}