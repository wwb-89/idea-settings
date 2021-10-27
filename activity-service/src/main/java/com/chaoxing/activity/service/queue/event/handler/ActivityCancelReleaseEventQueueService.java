package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityCancelReleaseEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.bigdata.BigDataPointApiService;
import com.chaoxing.activity.service.queue.BigDataPointQueue;
import com.chaoxing.activity.service.queue.activity.ActivityTimingReleaseQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityCancelReleaseEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 15:04:28
 */
@Slf4j
@Service
public class ActivityCancelReleaseEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityTimingReleaseQueue activityTimingReleaseQueue;
    @Resource
    private BigDataPointQueue bigDataPointQueue;

    public void handle(ActivityCancelReleaseEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        // 取消定时发布
        activityTimingReleaseQueue.remove(activityId);
        // 大数据积分（举办活动）
        bigDataPointQueue.push(new BigDataPointQueue.QueueParamDTO(activity.getCreateUid(), activity.getCreateFid(), activityId, BigDataPointApiService.PointTypeEnum.CANCEL_ORGANIZE_ACTIVITY.getValue()));
    }

}