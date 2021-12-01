package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserAddRatingEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserAddRatingEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserAddRatingEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户新增评价事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserAddRatingEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 17:49:56
 */
@Slf4j
@Component
public class UserAddRatingEventTask {

    @Resource
    private UserAddRatingEventQueue userAddRatingEventQueue;
    @Resource
    private UserAddRatingEventQueueService userAddRatingEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserAddRatingEventOrigin eventOrigin = userAddRatingEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userAddRatingEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户新增评价事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userAddRatingEventQueue.push(eventOrigin);
        }
    }

}