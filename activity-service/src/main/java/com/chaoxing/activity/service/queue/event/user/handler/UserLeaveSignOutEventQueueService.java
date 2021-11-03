package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserLeaveSignOutEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueue;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueue;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueue;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserLeaveSignOutEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-28 18:15:25
 */
@Slf4j
@Service
public class UserLeaveSignOutEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityStatSummaryQueue activityStatSummaryQueue;
    @Resource
    private UserStatSummaryQueue userStatSummaryQueue;
    @Resource
    private UserActionRecordQueue userActionRecordQueue;

    public void handle(UserLeaveSignOutEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Activity activity = activityQueryService.getBySignId(eventOrigin.getSignId());
        if (activity == null) {
            return;
        }
        Integer activityId = activity.getId();
        Integer uid = eventOrigin.getUid();
        // 活动统计汇总中的签到数量需要更新
        activityStatSummaryQueue.push(activityId);
        // 用户活动汇总统计中的报名签到信息需要更新
        userStatSummaryQueue.pushUserSignStat(new UserStatSummaryQueue.QueueParamDTO(uid, activityId));
        // 记录用户行为
        UserActionRecordQueue.QueueParamDTO queueParam = new UserActionRecordQueue.QueueParamDTO(uid, activityId, UserActionTypeEnum.SIGN_OUT, UserActionEnum.LEAVE_SIGNED_OUT, String.valueOf(eventOrigin.getSignOutId()), DateUtils.timestamp2Date(eventOrigin.getTimestamp()));
        userActionRecordQueue.push(queueParam);
    }

}
