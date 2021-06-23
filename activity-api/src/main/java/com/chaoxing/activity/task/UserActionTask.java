package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityIsAboutStartHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueueService;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueueService;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Objects;

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
    private UserActionRecordQueueService userActionRecordQueueService;
    @Resource
    private ActivityIsAboutStartHandleService activityIsAboutStartHandleService;

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
                userStatSummaryQueueService.pushUserSignStat(new UserStatSummaryQueueService.QueueParamDTO(uid, activityId));
                if (Objects.equals(UserActionEnum.SIGNED_UP, queueParam.getUserAction())) {
                    // 报名成功
                    activityIsAboutStartHandleService.sendSignedUpNotice(activity, new ArrayList(){{add(uid);}});
                }
                break;
            case SIGN_IN:
                // 活动统计汇总中的签到数量、签到率、平均签到时长需要更新
                activityStatSummaryQueueService.push(activityId);
                // 用户汇总统计
                userStatSummaryQueueService.pushUserSignStat(new UserStatSummaryQueueService.QueueParamDTO(uid, activityId));
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
                activityStatSummaryQueueService.push(activityId);
                userStatSummaryQueueService.pushUserResultStat(new UserStatSummaryQueueService.QueueParamDTO(uid, activityId));
                break;
            default:
                // 未知的用户行为
        }
        // 记录用户行为
        userActionRecordQueueService.push(queueParam);
    }

}