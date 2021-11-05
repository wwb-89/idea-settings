package com.chaoxing.activity.task.event.sign;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.sign.SignInDeletedEventOrigin;
import com.chaoxing.activity.service.queue.event.sign.SignInDeletedEventQueue;
import com.chaoxing.activity.service.queue.event.sign.handler.SignInDeletedEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignInDeletedEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 19:17:06
 */
@Slf4j
@Component
public class SignInDeletedEventTask {

    @Resource
    private SignInDeletedEventQueue signInDeletedEventQueue;
    @Resource
    private SignInDeletedEventQueueService signInDeletedEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        SignInDeletedEventOrigin eventOrigin = signInDeletedEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            signInDeletedEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理签到删除事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            signInDeletedEventQueue.push(eventOrigin);
        }
    }

}
