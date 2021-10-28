package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserSignedInEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserSignedInEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserSignedInEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserSignedInEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 10:48:18
 */
@Slf4j
@Component
public class UserSignedInEventTask {

    @Resource
    private UserSignedInEventQueue userSignedInEventQueue;
    @Resource
    private UserSignedInEventQueueService userSignedInEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserSignedInEventOrigin eventOrigin = userSignedInEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userSignedInEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户成功签到事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userSignedInEventQueue.push(eventOrigin);
        }
    }

}
