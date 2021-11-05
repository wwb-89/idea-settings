package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserDeleteRatingEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserDeleteRatingEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserDeleteRatingEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户删除评价事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserDeleteRatingEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:06:48
 */
@Slf4j
@Component
public class UserDeleteRatingEventTask {

    @Resource
    private UserDeleteRatingEventQueue userDeleteRatingEventQueue;
    @Resource
    private UserDeleteRatingEventQueueService userDeleteRatingEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        UserDeleteRatingEventOrigin eventOrigin = userDeleteRatingEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            userDeleteRatingEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户删除评价事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            userDeleteRatingEventQueue.push(eventOrigin);
        }
    }

}
