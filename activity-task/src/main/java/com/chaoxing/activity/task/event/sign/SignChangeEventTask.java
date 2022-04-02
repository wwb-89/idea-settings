package com.chaoxing.activity.task.event.sign;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.sign.SignChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.sign.SignChangeEventQueue;
import com.chaoxing.activity.service.queue.event.sign.handler.SignChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 19:35:51
 */
@Slf4j
@Component
public class SignChangeEventTask {

    @Resource
    private SignChangeEventQueue signChangeEventQueue;
    @Resource
    private SignChangeEventQueueService signChangeEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        SignChangeEventOrigin eventOrigin = signChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            signChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理报名签到改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            signChangeEventQueue.push(eventOrigin);
        }
    }

}
