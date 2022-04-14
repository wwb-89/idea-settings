package com.chaoxing.activity.service.queue.user.handler;

import com.chaoxing.activity.service.queue.user.UserActionRecordValidQueue;
import com.chaoxing.activity.service.user.action.UserActionRecordHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordValidQueueService
 * @description
 * @blame wwb
 * @date 2021-11-02 14:20:00
 */
@Slf4j
@Service
public class UserActionRecordValidQueueService {

    @Resource
    private UserActionRecordHandleService userActionRecordHandleService;

    public void handle(UserActionRecordValidQueue.QueueParamDTO queueParam) {
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
