package com.chaoxing.activity.task.user;

import com.chaoxing.activity.service.queue.user.UserActionQueue;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueue;
import com.chaoxing.activity.service.user.action.UserActionRecordHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**消费用户行为记录队列
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordTask
 * @description
 * @blame wwb
 * @date 2021-06-24 10:30:16
 */
@Slf4j
@Component
public class UserActionRecordTask {

    @Resource
    private UserActionRecordQueue userActionRecordQueue;
    @Resource
    private UserActionRecordHandleService userActionRecordHandleService;

    @Scheduled(fixedDelay = 1L)
    public void consumerUserActionRecord() throws InterruptedException {
        UserActionQueue.QueueParamDTO queueParam = userActionRecordQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userActionRecordHandleService.addUserActionRecord(queueParam);
        } catch (Exception e) {
            e.printStackTrace();
            userActionRecordQueue.push(queueParam);
        }
    }

}