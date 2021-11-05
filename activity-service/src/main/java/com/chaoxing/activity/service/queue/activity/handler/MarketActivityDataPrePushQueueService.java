package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushConfig;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.v2.DataPushConfigService;
import com.chaoxing.activity.service.queue.activity.OrgActivityDataPushQueue;
import com.chaoxing.activity.service.queue.activity.MarketActivityDataPushQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketActivityDataPrePushQueueService
 * @description 活动可能配置了很多推送，在此处分发
 * @blame wwb
 * @date 2021-10-29 16:45:31
 */
@Slf4j
@Service
public class MarketActivityDataPrePushQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private DataPushConfigService dataPushConfigService;
    @Resource
    private OrgActivityDataPushQueue orgActivityDataPushQueue;
    @Resource
    private MarketActivityDataPushQueue marketActivityDataPushQueue;

    public void handle(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer marketId = activity.getMarketId();
        if (marketId != null) {
            List<DataPushConfig> dataPushConfigs = dataPushConfigService.ListByMarketId(marketId, DataPushConfig.DataTypeEnum.ACTIVITY_DATA);
            if (CollectionUtils.isNotEmpty(dataPushConfigs)) {
                handleMarketDataPush(activity, dataPushConfigs);
                return;
            }
        }
        // 委托给机构配置的数据推送处理
        handleOrgDataPush(activityId);
    }

    private void handleMarketDataPush(Activity activity, List<DataPushConfig> dataPushConfigs) {
        for (DataPushConfig dataPushConfig : dataPushConfigs) {
            MarketActivityDataPushQueue.QueueParamDTO queueParam = new MarketActivityDataPushQueue.QueueParamDTO(activity.getId(), dataPushConfig.getId());
            marketActivityDataPushQueue.push(queueParam);
        }
    }

    private void handleOrgDataPush(Integer activityId) {
        orgActivityDataPushQueue.push(activityId);
    }

}