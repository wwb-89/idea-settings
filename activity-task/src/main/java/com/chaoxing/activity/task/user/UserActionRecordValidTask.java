package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserActionRecordValidQueue;
import com.chaoxing.activity.service.queue.user.handler.UserActionRecordValidQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户行为记录有效性更新任务
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordValidTask
 * @description
 * @blame wwb
 * @date 2021-06-24 14:59:15
 */
@Slf4j
@Component
public class UserActionRecordValidTask {

    @Resource
    private UserActionRecordValidQueue userActionRecordValidQueue;
    @Resource
    private UserActionRecordValidQueueService userActionRecordValidQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserActionRecordValidQueue.QueueParamDTO queueParam = userActionRecordValidQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userActionRecordValidQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户行为记录有效性更新任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            userActionRecordValidQueue.push(queueParam);
        }
    }

}