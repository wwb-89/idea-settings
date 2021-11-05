package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityReleaseEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.bigdata.BigDataPointApiService;
import com.chaoxing.activity.service.queue.BigDataPointQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 15:05:00
 */
@Slf4j
@Service
public class ActivityReleaseEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private BigDataPointQueue bigDataPointQueue;

    public void handle(ActivityReleaseEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        // 大数据积分（举办活动）
        bigDataPointQueue.push(new BigDataPointQueue.QueueParamDTO(activity.getCreateUid(), activity.getCreateFid(), activityId, BigDataPointApiService.PointTypeEnum.ORGANIZE_ACTIVITY.getValue()));
    }

}
