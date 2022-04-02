package com.chaoxing.activity.task.event.sign;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.sign.SignUpAddEventOrigin;
import com.chaoxing.activity.service.queue.event.sign.SignUpAddEventQueue;
import com.chaoxing.activity.service.queue.event.sign.handler.SignUpAddEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignUpAddEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 19:18:04
 */
@Slf4j
@Component
public class SignUpAddEventTask {

    @Resource
    private SignUpAddEventQueue signUpAddEventQueue;
    @Resource
    private SignUpAddEventQueueService signUpAddEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        SignUpAddEventOrigin eventOrigin = signUpAddEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            signUpAddEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理报名新增事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            signUpAddEventQueue.push(eventOrigin);
        }
    }

}
