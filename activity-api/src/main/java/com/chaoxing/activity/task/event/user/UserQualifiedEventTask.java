package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserQualifiedEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserQualifiedEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserQualifiedEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户合格事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserQualifiedEventTask
 * @description
 * @blame wwb
 * @date 2021-11-01 10:51:20
 */
@Slf4j
@Component
public class UserQualifiedEventTask {

    @Resource
    private UserQualifiedEventQueue userQualifiedEventQueue;
    @Resource
    private UserQualifiedEventQueueService userQualifiedEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserQualifiedEventOrigin eventOrigin = userQualifiedEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userQualifiedEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户合格事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userQualifiedEventQueue.push(eventOrigin);
        }
    }

}
