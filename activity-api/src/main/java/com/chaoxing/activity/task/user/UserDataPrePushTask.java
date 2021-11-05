package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserDataPrePushQueue;
import com.chaoxing.activity.service.queue.user.handler.UserDataPrePushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户数据预推送任务
 * @author wwb
 * @version ver 1.0
 * @className UserDataPrePushTask
 * @description
 * @blame wwb
 * @date 2021-11-02 16:05:48
 */
@Slf4j
@Component
public class UserDataPrePushTask {

    @Resource
    private UserDataPrePushQueue userDataPrePushQueue;
    @Resource
    private UserDataPrePushQueueService userDataPrePushQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserDataPrePushQueue.QueueParamDTO queueParam = userDataPrePushQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userDataPrePushQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户数据预推送任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            userDataPrePushQueue.push(queueParam);
        }
    }

}
