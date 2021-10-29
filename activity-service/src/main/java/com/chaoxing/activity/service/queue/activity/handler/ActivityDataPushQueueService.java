package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityDataPushQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动数据推送
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataPushQueueService
 * @description
 * @blame wwb
 * @date 2021-10-29 17:00:56
 */
@Slf4j
@Service
public class ActivityDataPushQueueService {

    @Resource
    private ActivityQueryService activityQueryService;

    public void handle(ActivityDataPushQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }

    }

}