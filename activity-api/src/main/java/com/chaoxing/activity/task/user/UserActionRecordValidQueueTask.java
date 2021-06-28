package com.chaoxing.activity.task.user;

import com.chaoxing.activity.service.queue.user.UserActionRecordValidQueueService;
import com.chaoxing.activity.service.user.action.UserActionRecordHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户行为记录有效性更新
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordValidQueueTask
 * @description
 * @blame wwb
 * @date 2021-06-24 14:59:15
 */
@Slf4j
@Component
public class UserActionRecordValidQueueTask {

    @Resource
    private UserActionRecordValidQueueService userActionRecordValidQueueService;
    @Resource
    private UserActionRecordHandleService userActionRecordHandleService;

    @Scheduled(fixedDelay = 1L)
    public void consumerUserActionRecordValid() throws InterruptedException {
        UserActionRecordValidQueueService.QueueParamDTO queueParam = userActionRecordValidQueueService.pop();
        if (queueParam == null) {
            return;
        }
        Boolean valid = queueParam.getValid();
        if (valid == null) {
            return;
        }
        Integer activityId = queueParam.getActivityId();
        String identify = queueParam.getIdentify();
        if (valid) {
            userActionRecordHandleService.enableUserActionRecord(activityId, identify);
        } else {
            userActionRecordHandleService.disableUserActionRecord(activityId, identify);
        }
    }

}