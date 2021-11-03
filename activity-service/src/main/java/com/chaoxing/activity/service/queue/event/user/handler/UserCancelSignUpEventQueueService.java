package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserCancelSignUpEventOrigin;
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
 * @className UserCancelSignUpEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-28 18:00:43
 */
@Slf4j
@Service
public class UserCancelSignUpEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityStatSummaryQueue activityStatSummaryQueue;
    @Resource
    private UserStatSummaryQueue userStatSummaryQueue;
    @Resource
    private UserActionRecordQueue userActionRecordQueue;

    public void handle(UserCancelSignUpEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer signId = eventOrigin.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity == null) {
            return;
        }
        Integer activityId = activity.getId();
        Integer uid = eventOrigin.getUid();
        // 相应活动的统计数据需要变更
        activityStatSummaryQueue.push(activityId);
        // 用户汇总表的报名签到统计信息需要更新
        userStatSummaryQueue.pushUserSignStat(new UserStatSummaryQueue.QueueParamDTO(uid, activityId));
        // 记录用户行为
        UserActionRecordQueue.QueueParamDTO queueParam = new UserActionRecordQueue.QueueParamDTO(uid, activityId, UserActionTypeEnum.SIGN_UP, UserActionEnum.CANCEL_SIGNED_UP, String.valueOf(eventOrigin.getSignUpId()), DateUtils.timestamp2Date(eventOrigin.getTimestamp()));
        userActionRecordQueue.push(queueParam);
    }

}
