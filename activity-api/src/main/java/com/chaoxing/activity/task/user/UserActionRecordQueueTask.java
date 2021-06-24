package com.chaoxing.activity.task.user;

import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueueService;
import com.chaoxing.activity.service.user.action.UserActionRecordHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**消费用户行为记录队列
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordQueueTask
 * @description
 * @blame wwb
 * @date 2021-06-24 10:30:16
 */
@Slf4j
@Component
public class UserActionRecordQueueTask {

    @Resource
    private UserActionRecordQueueService userActionRecordQueueService;
    @Resource
    private UserActionRecordHandleService userActionRecordHandleService;

    @Scheduled(fixedDelay = 1L)
    public void consumerUserActionRecord() throws InterruptedException {
        UserActionQueueService.QueueParamDTO queueParam = userActionRecordQueueService.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userActionRecordHandleService.addUserActionRecord(queueParam);
        } catch (Exception e) {
            e.printStackTrace();
            userActionRecordQueueService.push(queueParam);
        }
    }

}