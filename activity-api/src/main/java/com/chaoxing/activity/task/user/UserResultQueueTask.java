package com.chaoxing.activity.task.user;

import com.chaoxing.activity.service.queue.user.UserResultQueueService;
import com.chaoxing.activity.service.user.result.UserResultHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户成绩队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultQueueTask
 * @description 重新计算用户的成绩
 * @blame wwb
 * @date 2021-06-24 14:55:07
 */
@Slf4j
@Component
public class UserResultQueueTask {

    @Resource
    private UserResultQueueService userResultQueueService;
    @Resource
    private UserResultHandleService userResultHandleService;

    @Scheduled(fixedDelay = 1L)
    public void consumerUserResult() throws InterruptedException {
        UserResultQueueService.QueueParamDTO queueParam = userResultQueueService.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userResultHandleService.updateUserResult(queueParam.getUid(), queueParam.getActivityId());
        } catch (Exception e) {
            e.printStackTrace();
            userResultQueueService.push(queueParam);
        }
    }

}