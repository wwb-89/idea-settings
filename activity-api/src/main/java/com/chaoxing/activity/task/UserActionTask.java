package com.chaoxing.activity.task;

import com.chaoxing.activity.service.queue.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.UserActionQueueService;
import lombok.extern.slf4j.Slf4j;
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

    public void userSignUpActionHandle() {
        UserActionQueueService.UserActionDTO userAction = userSignActionQueueService.getUserSignUpAction();
        if (userAction== null) {
            return;
        }
        // TODO 分发
    }

    public void userSignInActionHandle() {
        UserActionQueueService.UserActionDTO userAction = userSignActionQueueService.getUserSignInAction();
        if (userAction== null) {
            return;
        }
        // TODO 分发
    }

    public void userResultActionHandle() {
        UserActionQueueService.UserActionDTO userAction = userSignActionQueueService.getUserResultAction();
        if (userAction== null) {
            return;
        }
        // TODO 分发
    }

}