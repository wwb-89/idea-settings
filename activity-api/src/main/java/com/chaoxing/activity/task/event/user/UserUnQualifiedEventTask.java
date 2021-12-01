package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserUnQualifiedEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserUnQualifiedEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserUnQualifiedEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户不合格事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserUnQualifiedEventTask
 * @description
 * @blame wwb
 * @date 2021-11-01 10:50:26
 */
@Slf4j
@Component
public class UserUnQualifiedEventTask {

    @Resource
    private UserUnQualifiedEventQueue userUnQualifiedEventQueue;
    @Resource
    private UserUnQualifiedEventQueueService userUnQualifiedEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserUnQualifiedEventOrigin eventOrigin = userUnQualifiedEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userUnQualifiedEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户不合格事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userUnQualifiedEventQueue.push(eventOrigin);
        }
    }

}
