package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserDataPushQueue;
import com.chaoxing.activity.service.queue.user.handler.UserDataPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户数据推送任务
 * @author wwb
 * @version ver 1.0
 * @className UserDataPushTask
 * @description
 * @blame wwb
 * @date 2021-11-02 15:58:11
 */
@Slf4j
@Component
public class UserDataPushTask {

    @Resource
    private UserDataPushQueue userDataPushQueue;
    @Resource
    private UserDataPushQueueService userDataPushQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserDataPushQueue.QueueParamDTO queueParam = userDataPushQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userDataPushQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户数据推送任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            userDataPushQueue.delayPush(queueParam);
        }
    }

}
