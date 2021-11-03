package com.chaoxing.activity.service.queue.user.handler;

import com.chaoxing.activity.service.queue.user.UserActionRecordQueue;
import com.chaoxing.activity.service.user.action.UserActionRecordHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordQueueService
 * @description
 * @blame wwb
 * @date 2021-11-02 13:54:57
 */
@Slf4j
@Service
public class UserActionRecordQueueService {

    @Resource
    private UserActionRecordHandleService userActionRecordHandleService;

    public void handle(UserActionRecordQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        userActionRecordHandleService.addUserActionRecord(queueParam);
    }

}
