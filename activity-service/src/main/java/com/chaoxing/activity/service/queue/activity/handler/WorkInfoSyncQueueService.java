package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className WorkInfoSyncQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 16:17:54
 */
@Service
public class WorkInfoSyncQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private WorkApiService workApiService;

    public void handle(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer workId = activity.getWorkId();
        workApiService.updateWorkInfo(workId, activity.getName(), activity.getStartTime(), activity.getEndTime(), activity.getCreateUid());
    }

}
