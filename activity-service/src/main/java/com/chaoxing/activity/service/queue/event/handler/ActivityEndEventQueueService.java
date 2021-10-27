package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityEndEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.BigDataPointTaskQueue;
import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueue;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndEventQueueService
 * @description
 * 1、大数据积分推送
 * @blame wwb
 * @date 2021-10-27 16:25:46
 */
@Slf4j
@Service
public class ActivityEndEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityInspectionResultDecideQueue activityInspectionResultDecideQueueService;
    @Resource
    private BlacklistAutoAddQueue blacklistAutoAddQueueService;
    @Resource
    private BigDataPointTaskQueue bigDataPointTaskQueueService;

    public void handle(ActivityEndEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer oldStatus = eventOrigin.getOldStatus();
        // 当活动结束时触发用户合格判定
        activityInspectionResultDecideQueueService.push(activityId);
        // 当活动结束时触发黑名单判定
        blacklistAutoAddQueueService.push(new BlacklistAutoAddQueue.QueueParamDTO(activityId));
        // 活动结束，大数据积分推送
        BigDataPointTaskQueue.QueueParamDTO queueParam = new BigDataPointTaskQueue.QueueParamDTO(activity.getId(), activity.getCreateFid(), true);
        bigDataPointTaskQueueService.push(queueParam);
        if (Objects.equals(Activity.StatusEnum.ENDED.getValue(), oldStatus)) {
            // 活动从结束状态变为其他状态时需要删除大数据积分已经推送的数据
            BigDataPointTaskQueue.QueueParamDTO bigDataPointTaskQueueParam = new BigDataPointTaskQueue.QueueParamDTO(activity.getId(), activity.getCreateFid(), false);
            bigDataPointTaskQueueService.push(bigDataPointTaskQueueParam);
        }
    }

}
