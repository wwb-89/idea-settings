package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserCancelSignUpEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserCancelSignUpEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserCancelSignUpEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户取消报名事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignUpEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:01:09
 */
@Slf4j
@Component
public class UserCancelSignUpEventTask {

    @Resource
    private UserCancelSignUpEventQueue userCancelSignUpEventQueue;
    @Resource
    private UserCancelSignUpEventQueueService userCancelSignUpEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserCancelSignUpEventOrigin eventOrigin = userCancelSignUpEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userCancelSignUpEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户取消报名事件任务队列error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userCancelSignUpEventQueue.push(eventOrigin);
        }
    }

}
