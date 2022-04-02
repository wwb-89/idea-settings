package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueue;
import com.chaoxing.activity.service.queue.user.handler.UserActionRecordQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户行为记录任务
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
    private UserActionRecordQueueService userActionRecordQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserActionRecordQueue.QueueParamDTO queueParam = userActionRecordQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userActionRecordQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户行为记录任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            userActionRecordQueue.push(queueParam);
        }
    }

}