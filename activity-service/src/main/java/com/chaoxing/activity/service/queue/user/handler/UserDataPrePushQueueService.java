package com.chaoxing.activity.service.queue.user.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushConfig;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.v2.DataPushConfigService;
import com.chaoxing.activity.service.queue.activity.MarketActivityDataPushQueue;
import com.chaoxing.activity.service.queue.activity.OrgActivityDataPushQueue;
import com.chaoxing.activity.service.queue.user.UserDataPrePushQueue;
import com.chaoxing.activity.service.queue.user.UserDataPushQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserDataPrePushQueueService
 * @description
 * @blame wwb
 * @date 2021-11-02 15:40:03
 */
@Slf4j
@Service
public class UserDataPrePushQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private DataPushConfigService dataPushConfigService;
    @Resource
    private OrgActivityDataPushQueue orgActivityDataPushQueue;
    @Resource
    private MarketActivityDataPushQueue activityDataPushQueue;
    @Resource
    private UserDataPushQueue userDataPushQueue;

    public void handle(UserDataPrePushQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer uid = queueParam.getUid();
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer marketId = activity.getMarketId();
        if (marketId != null) {
            List<DataPushConfig> dataPushConfigs = dataPushConfigService.ListByMarketId(marketId, DataPushConfig.DataTypeEnum.USER_DATA);
            if (CollectionUtils.isNotEmpty(dataPushConfigs)) {
                handleMarketDataPush(uid, activityId, dataPushConfigs);
                return;
            }
        }
        // 委托给机构配置的数据推送处理
        handleOrgDataPush(uid, activityId);

    }

    private void handleMarketDataPush(Integer uid, Integer activityId, List<DataPushConfig> dataPushConfigs) {
        for (DataPushConfig dataPushConfig : dataPushConfigs) {
            UserDataPushQueue.QueueParamDTO queueParam = new UserDataPushQueue.QueueParamDTO(uid, activityId, dataPushConfig.getId());
            userDataPushQueue.push(queueParam);
        }
    }

    private void handleOrgDataPush(Integer uid, Integer activityId) {

    }

}
