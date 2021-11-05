package com.chaoxing.activity.task.event.sign;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.sign.SignUpDeletedEventOrigin;
import com.chaoxing.activity.service.queue.event.sign.SignUpDeletedEventQueue;
import com.chaoxing.activity.service.queue.event.sign.handler.SignUpDeletedEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignUpDeletedEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 19:18:57
 */
@Slf4j
@Component
public class SignUpDeletedEventTask {

    @Resource
    private SignUpDeletedEventQueue signUpDeletedEventQueue;
    @Resource
    private SignUpDeletedEventQueueService signUpDeletedEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        SignUpDeletedEventOrigin eventOrigin = signUpDeletedEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            signUpDeletedEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理报名删除事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            signUpDeletedEventQueue.push(eventOrigin);
        }
    }

}
