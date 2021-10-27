package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityChangeEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 15:30:51
 */
@Slf4j
@Service
public class ActivityChangeEventQueueService {

    @Resource
    private DataPushService dataPushService;
    @Resource
    private ActivityQueryService activityQueryService;

    public void handle(ActivityChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        // 数据推送
        DataPushService.DataPushParamDTO dataPushParam = new DataPushService.DataPushParamDTO(activity.getCreateFid(), OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, String.valueOf(activityId));
        dataPushService.dataPush(dataPushParam);
    }

}