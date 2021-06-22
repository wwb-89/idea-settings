package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.user.UserActionDetailQueueService;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueueService;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**用户行为任务
 * @author wwb
 * @version ver 1.0
 * @className UserActionTask
 * @description
 * @blame wwb
 * @date 2021-05-25 19:44:16
 */
@Slf4j
@Component
public class UserActionTask {

    @Resource
    private UserActionQueueService userSignActionQueueService;
    @Resource
    private ActivityStatSummaryQueueService activityStatSummaryQueueService;
    @Resource
    private UserStatSummaryQueueService userStatSummaryQueueService;
    @Resource
    private UserActionDetailQueueService userActionDetailQueueService;

    @Resource
    private ActivityQueryService activityQueryService;

    @Scheduled(fixedDelay = 1L)
    public void userSignUpActionHandle() throws InterruptedException {
        UserActionQueueService.QueueParamDTO queueParam = userSignActionQueueService.getUserSignUpAction();
        if (queueParam == null) {
            return;
        }
        // 用户统计（用户参与的活动数）
        Integer signId = queueParam.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            Integer activityId = activity.getId();
            activityStatSummaryQueueService.pushSignStat(activityId);
            Integer uid = queueParam.getUid();
            userStatSummaryQueueService.addUserSignStat(UserStatSummaryQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).build());
            // 用户报名行为详情更新
            Integer signUpId = queueParam.getSignUpId();
            LocalDateTime time = queueParam.getTime();
            userActionDetailQueueService.push(UserActionDetailQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).activityId(activityId).identify(String.valueOf(signUpId)).userActionType(UserActionTypeEnum.SIGN_UP).userAction(queueParam.getUserAction()).time(time).build());
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void userSignInActionHandle() throws InterruptedException {
        UserActionQueueService.QueueParamDTO queueParam = userSignActionQueueService.getUserSignInAction();
        if (queueParam == null) {
            return;
        }
        // 分发到活动统计和用户统计
        Integer signId = queueParam.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            Integer activityId = activity.getId();
            activityStatSummaryQueueService.pushSignStat(activityId);
            Integer uid = queueParam.getUid();
            userStatSummaryQueueService.addUserSignStat(UserStatSummaryQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).build());
            // 用户签到行为详情更新
            Integer signInId = queueParam.getSignInId();
            LocalDateTime time = queueParam.getTime();
            userActionDetailQueueService.push(UserActionDetailQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).identify(String.valueOf(signInId)).userActionType(UserActionTypeEnum.SIGN_IN).userAction(queueParam.getUserAction()).time(time).build());
        }
    }

    @Scheduled(fixedDelay = 1L)
    public void userResultActionHandle() throws InterruptedException {
        UserActionQueueService.QueueParamDTO queueParam = userSignActionQueueService.getUserResultAction();
        if (queueParam == null) {
            return;
        }
        // 分发到活动统计和用户统计
        Integer signId = queueParam.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            activityStatSummaryQueueService.addResultStat(activity.getId());
            Integer uid = queueParam.getUid();
            userStatSummaryQueueService.addUserResultStat(UserStatSummaryQueueService.QueueParamDTO.builder().uid(uid).activityId(activity.getId()).build());
        }
    }

}