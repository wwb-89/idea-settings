package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityTimingReleaseQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimingReleaseQueueService
 * @description
 * @blame wwb
 * @date 2021-10-29 16:28:15
 */
@Slf4j
@Service
public class ActivityTimingReleaseQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityHandleService activityHandleService;

    public void handle(ActivityTimingReleaseQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Boolean timingRelease = Optional.ofNullable(activity.getTimingRelease()).orElse(false);
        if (!timingRelease) {
            return;
        }
        if (activity.getTimingReleaseTime().compareTo(queueParam.getReleaseTime()) != 0) {
            // 发布时间不一致
            return;
        }
        activityHandleService.release(queueParam.getActivityId(), queueParam.getLoginUser());
    }

}
