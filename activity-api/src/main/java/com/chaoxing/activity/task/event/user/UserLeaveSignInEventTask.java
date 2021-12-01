package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserLeaveSignInEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserLeaveSignInEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserLeaveSignInEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户签到请假事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserLeaveSignInEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:13:00
 */
@Slf4j
@Component
public class UserLeaveSignInEventTask {

    @Resource
    private UserLeaveSignInEventQueue userLeaveSignInEventQueue;
    @Resource
    private UserLeaveSignInEventQueueService userLeaveSignInEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserLeaveSignInEventOrigin eventOrigin = userLeaveSignInEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userLeaveSignInEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户签到请假事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userLeaveSignInEventQueue.push(eventOrigin);
        }
    }

}