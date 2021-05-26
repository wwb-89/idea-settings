package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.UserActionQueueService;
import com.chaoxing.activity.service.queue.UserStatSummaryQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
    private ActivityQueryService activityQueryService;

    public void userSignUpActionHandle() {
        UserActionQueueService.UserActionDTO userAction = userSignActionQueueService.getUserSignUpAction();
        if (userAction == null) {
            return;
        }
        // 分发到活动统计和用户统计

    }

    @Scheduled(fixedDelay = 1L)
    public void userSignInActionHandle() {
        UserActionQueueService.UserActionDTO userAction = userSignActionQueueService.getUserSignInAction();
        if (userAction == null) {
            return;
        }
        // 分发到活动统计和用户统计
        Integer signId = userAction.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            activityStatSummaryQueueService.addSignInStat(activity.getId());
        }
        Integer uid = userAction.getUid();
        userStatSummaryQueueService.addUserSignInStat(uid);
    }

    @Scheduled(fixedDelay = 1L)
    public void userResultActionHandle() {
        UserActionQueueService.UserActionDTO userAction = userSignActionQueueService.getUserResultAction();
        if (userAction == null) {
            return;
        }
        // 分发到活动统计和用户统计
        Integer signId = userAction.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            activityStatSummaryQueueService.addResultStat(activity.getId());
        }
        Integer uid = userAction.getUid();
        userStatSummaryQueueService.addUserResultStat(uid);
    }

}