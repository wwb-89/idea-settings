package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityNameChangedEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.MhApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-26 18:35:12
 */
@Slf4j
@Service
public class ActivityNameChangeEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private MhApiService mhApiService;

    public void handle(ActivityNameChangedEventOrigin eventOrigin) {
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer pageId = activity.getPageId();
        if (pageId == null) {
            return;
        }
        mhApiService.updateWebTitle(pageId, activity.getName(), activity.getCreateUid());
    }

}
