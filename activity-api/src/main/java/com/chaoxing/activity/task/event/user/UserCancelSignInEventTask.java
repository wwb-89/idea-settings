package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserCancelSignInEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserCancelSignInEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserCancelSignInEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户取消签到事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignInEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 17:53:19
 */
@Slf4j
@Component
public class UserCancelSignInEventTask {

    @Resource
    private UserCancelSignInEventQueue userCancelSignInEventQueue;
    @Resource
    private UserCancelSignInEventQueueService userCancelSignInEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserCancelSignInEventOrigin eventOrigin = userCancelSignInEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userCancelSignInEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户取消签到事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userCancelSignInEventQueue.push(eventOrigin);
        }
    }

}
