package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityDeletedEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushService;
import com.chaoxing.activity.service.manager.bigdata.BigDataPointApiService;
import com.chaoxing.activity.service.queue.BigDataPointQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityDeletedEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 17:46:06
 */
@Slf4j
@Service
public class ActivityDeletedEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private DataPushService dataPushService;
    @Resource
    private BigDataPointQueue bigDataPointQueue;

    public void handle(ActivityDeletedEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        // 活动数据推送
        Integer createFid = activity.getCreateFid();
        dataPushService.dataPush(new DataPushService.DataPushParamDTO(createFid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, String.valueOf(activityId)));
        // 大数据积分（举办活动），必须是已发布的活动
        if (activity.getReleased()) {
            bigDataPointQueue.push(new BigDataPointQueue.QueueParamDTO(activity.getCreateUid(), activity.getCreateFid(), activityId, BigDataPointApiService.PointTypeEnum.CANCEL_ORGANIZE_ACTIVITY.getValue()));
        }
    }

}
