package com.chaoxing.activity.task.event.sign;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.sign.SignInAddEventOrigin;
import com.chaoxing.activity.service.queue.event.sign.SignInAddEventQueue;
import com.chaoxing.activity.service.queue.event.sign.handler.SignInAddEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignInAddEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 19:15:15
 */
@Slf4j
@Component
public class SignInAddEventTask {

    @Resource
    private SignInAddEventQueue signInAddEventQueue;
    @Resource
    private SignInAddEventQueueService signInAddEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        SignInAddEventOrigin eventOrigin = signInAddEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            signInAddEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理签到新增事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            signInAddEventQueue.push(eventOrigin);
        }
    }

}