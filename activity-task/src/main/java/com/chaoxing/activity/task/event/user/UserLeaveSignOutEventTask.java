package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserLeaveSignOutEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserLeaveSignOutEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserLeaveSignOutEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户签退请假事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserLeaveSignOutEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:16:01
 */
@Slf4j
@Component
public class UserLeaveSignOutEventTask {

    @Resource
    private UserLeaveSignOutEventQueue userLeaveSignOutEventQueue;
    @Resource
    private UserLeaveSignOutEventQueueService userLeaveSignOutEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserLeaveSignOutEventOrigin eventOrigin = userLeaveSignOutEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userLeaveSignOutEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户签退请假事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userLeaveSignOutEventQueue.push(eventOrigin);
        }
    }

}
