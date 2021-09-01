package com.chaoxing.activity.task.user;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.event.UserSignedUpEventService;
import com.chaoxing.activity.service.queue.IntegralPushQueueService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueueService;
import com.chaoxing.activity.service.queue.user.UserRatingQueueService;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueueService;
import com.chaoxing.activity.util.enums.IntegralOriginTypeEnum;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**用户行为任务
 * @author wwb
 * @version ver 1.0
 * @className UserActionTask
 * @description 处理用户的行为（行为的来源为：第三方系统、活动引擎本身）参考 UserActionTypeEnum和UserActionEnum枚举
 * @blame wwb
 * @date 2021-05-25 19:44:16
 */
@Slf4j
@Component
public class UserActionTask {

    @Resource
    private ActivityStatSummaryQueueService activityStatSummaryQueueService;
    @Resource
    private UserStatSummaryQueueService userStatSummaryQueueService;
    @Resource
    private UserActionQueueService userActionQueueService;
    @Resource
    private UserActionRecordQueueService userActionRecordQueueService;
    @Resource
    private IntegralPushQueueService integralPushQueueService;
    @Resource
    private UserRatingQueueService userRatingQueueService;
    @Resource
    private UserSignedUpEventService userSignedUpEventService;

    @Resource
    private ActivityQueryService activityQueryService;

    /**处理用户行为
     * @Description 
     * @author wwb
     * @Date 2021-06-21 15:56:57
     * @param 
     * @return void
    */
    @Scheduled(fixedDelay = 1L)
    public void handleUserAction() throws InterruptedException {
        UserActionQueueService.QueueParamDTO queueParam = userActionQueueService.pop();
        if (queueParam == null) {
            return;
        }
        UserActionTypeEnum userActionType = queueParam.getUserActionType();
        Integer uid = queueParam.getUid();
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        switch (userActionType) {
            case SIGN_UP:
                // 活动统计汇总中的报名数量需要更新
                activityStatSummaryQueueService.push(activityId);
                // 用户活动汇总统计中的报名签到信息需要更新
                userStatSummaryQueueService.pushUserSignStat(new UserStatSummaryQueueService.QueueParamDTO(uid, activityId));
                if (Objects.equals(UserActionEnum.SIGNED_UP, queueParam.getUserAction())) {
                    // 报名成功
                    userSignedUpEventService.handle(activity, uid);
                    // 推送积分
                    integralPushQueueService.push(new IntegralPushQueueService.IntegralPushDTO(uid, activity.getCreateFid(), IntegralOriginTypeEnum.VIEW_ACTIVITY.getValue(), String.valueOf(activityId), activity.getName()));
                }
                break;
            case SIGN_IN:
                // 活动统计汇总中的报名数量需要更新
                activityStatSummaryQueueService.push(activityId);
                // 用户活动汇总统计中的报名签到信息需要更新
                userStatSummaryQueueService.pushUserSignStat(new UserStatSummaryQueueService.QueueParamDTO(uid, activityId));
                break;
            case RATING:
                // 通知报名签到用户评价了活动
                userRatingQueueService.push(new UserRatingQueueService.QueueParamDTO(uid, activity.getSignId()));
                break;
            case DISCUSS:
                break;
            case WORK:
                break;
            case PERFORMANCE:
                break;
            case QUALIFIED:
                // 活动汇总统计中合格的数量需要更新
                activityStatSummaryQueueService.push(activityId);
                // 用户活动汇总统计中的是否合格信息需要更新
                userStatSummaryQueueService.pushUserResultStat(new UserStatSummaryQueueService.QueueParamDTO(uid, activityId));
                break;
            default:
                // 其他未知的用户行为
        }
        // 记录用户行为
        userActionRecordQueueService.push(queueParam);
    }

}