package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserSignQueueService;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueueService;
import com.chaoxing.activity.service.user.action.UserActionHandleService;
import com.chaoxing.activity.util.enums.UserActionEnum;
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
    private ActivityStatSummaryQueueService activityStatSummaryQueueService;
    @Resource
    private UserStatSummaryQueueService userStatSummaryQueueService;
    @Resource
    private UserActionQueueService userActionQueueService;
    @Resource
    private UserActionHandleService userActionHandleService;

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
        switch (userActionType) {
            case SIGN_UP:
                userStatSummaryQueueService.addUserSignStat(UserStatSummaryQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).build());
                break;
            case SIGN_IN:
                // 活动汇总统计
                activityStatSummaryQueueService.addSignInStat(activityId);
                // 用户汇总统计
                userStatSummaryQueueService.addUserSignStat(UserStatSummaryQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).build());
                break;
            case RATING:
                break;
            case DISCUSS:
                break;
            case WORK:
                break;
            case PERFORMANCE:
                break;
            case QUALIFIED:
                // 合格判定
                activityStatSummaryQueueService.addResultStat(activityId);
                userStatSummaryQueueService.addUserResultStat(UserStatSummaryQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).build());
                break;
            default:
                // 未知的用户行为
        }
        // 记录用户行为
        userActionHandleService.updateUserAction(queueParam);
    }

}