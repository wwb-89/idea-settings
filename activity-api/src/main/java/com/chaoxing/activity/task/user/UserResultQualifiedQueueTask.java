package com.chaoxing.activity.task.user;

import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.user.UserResultQualifiedQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户成绩合格变更
 * @author wwb
 * @version ver 1.0
 * @className UserResultQualifiedQueueTask
 * @description
 * @blame wwb
 * @date 2021-06-25 10:11:29
 */
@Slf4j
@Component
public class UserResultQualifiedQueueTask {

    @Resource
    private UserResultQualifiedQueueService userResultQualifiedQueueService;
    @Resource
    private SignApiService signApiService;

    @Scheduled(fixedDelay = 1L)
    public void consumerUserResultQualifiedQueue() throws InterruptedException {
        UserResultQualifiedQueueService.QueueParamDTO queueParam = userResultQualifiedQueueService.pop();
        if (queueParam == null) {
            return;
        }
        try {
            signApiService.noticeSignUserResultChange(queueParam.getUid(), queueParam.getSignId());
        } catch (Exception e) {
            e.printStackTrace();
            userResultQualifiedQueueService.push(queueParam);
        }
    }

}