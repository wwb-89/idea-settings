package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserSignedOutEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserSignedOutEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserSignedOutEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户成功签退事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserSignedOutEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:19:20
 */
@Slf4j
@Component
public class UserSignedOutEventTask {

    @Resource
    private UserSignedOutEventQueue userSignedOutEventQueue;
    @Resource
    private UserSignedOutEventQueueService userSignedOutEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserSignedOutEventOrigin eventOrigin = userSignedOutEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userSignedOutEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户成功签退事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userSignedOutEventQueue.push(eventOrigin);
        }
    }

}
