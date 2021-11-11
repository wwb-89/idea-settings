package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserUnQualifiedEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueue;
import com.chaoxing.activity.service.queue.user.UserSignStatSummaryQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserUnQualifiedEventQueueService
 * @description
 * @blame wwb
 * @date 2021-11-01 10:48:47
 */
@Slf4j
@Service
public class UserUnQualifiedEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityStatSummaryQueue activityStatSummaryQueue;
    @Resource
    private UserSignStatSummaryQueue userSignStatSummaryQueue;

    public void handle(UserUnQualifiedEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer uid = eventOrigin.getUid();
        // 相应活动的统计数据需要变更
        activityStatSummaryQueue.push(activityId);
        // 用户汇总表的报名签到统计信息需要更新
        userSignStatSummaryQueue.push(new UserSignStatSummaryQueue.QueueParamDTO(uid, activityId));
    }

}
