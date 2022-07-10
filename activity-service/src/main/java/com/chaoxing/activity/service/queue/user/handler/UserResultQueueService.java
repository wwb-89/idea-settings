package com.chaoxing.activity.service.queue.user.handler;

import com.chaoxing.activity.service.queue.user.UserResultQueue;
import com.chaoxing.activity.service.user.result.UserResultHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户成绩更新队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultQueueService
 * @description
 * @blame wwb
 * @date 2021-11-02 14:24:00
 */
@Slf4j
@Service
public class UserResultQueueService {

    @Resource
    private UserResultHandleService userResultHandleService;

    public void handle(UserResultQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        userResultHandleService.updateUserResult(queueParam.getUid(), queueParam.getActivityId());
    }

}
