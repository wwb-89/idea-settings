package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserCancelSignOutEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserCancelSignOutEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserCancelSignOutEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户取消签退事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignOutEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 17:57:14
 */
@Slf4j
@Component
public class UserCancelSignOutEventTask {

    @Resource
    private UserCancelSignOutEventQueue userCancelSignOutEventQueue;
    @Resource
    private UserCancelSignOutEventQueueService userCancelSignOutEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserCancelSignOutEventOrigin eventOrigin = userCancelSignOutEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userCancelSignOutEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户取消签退事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userCancelSignOutEventQueue.push(eventOrigin);
        }
    }

}
