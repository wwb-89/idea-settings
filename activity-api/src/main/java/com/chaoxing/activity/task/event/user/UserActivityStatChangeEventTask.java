package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserActivityStatChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserActivityStatChangeEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserActivityStatChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户活动统计数据改变事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserActivityStatChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-29 16:10:19
 */
@Slf4j
@Component
public class UserActivityStatChangeEventTask {

    @Resource
    private UserActivityStatChangeEventQueue userActivityStatChangeEventQueue;
    @Resource
    private UserActivityStatChangeEventQueueService userActivityStatChangeEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserActivityStatChangeEventOrigin eventOrigin = userActivityStatChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userActivityStatChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户活动统计数据改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userActivityStatChangeEventQueue.push(eventOrigin);
        }
    }

}
