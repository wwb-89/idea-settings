package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserSignedUpEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserSignedUpEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserSignedUpEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户成功报名事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserSignedUpEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:21:56
 */
@Slf4j
@Component
public class UserSignedUpEventTask {

    @Resource
    private UserSignedUpEventQueue userSignedUpEventQueue;
    @Resource
    private UserSignedUpEventQueueService userSignedUpEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserSignedUpEventOrigin eventOrigin = userSignedUpEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userSignedUpEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户成功报名事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userSignedUpEventQueue.push(eventOrigin);
        }
    }

}
