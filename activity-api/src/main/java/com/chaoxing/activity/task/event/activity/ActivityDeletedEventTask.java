package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityDeletedEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityDeletedEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityDeletedEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动删除事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDeletedEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 17:45:32
 */
@Slf4j
@Component
public class ActivityDeletedEventTask {

    @Resource
    private ActivityDeletedEventQueue activityDeletedEventQueue;
    @Resource
    private ActivityDeletedEventQueueService activityDeletedEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动删除事件任务 start");
        ActivityDeletedEventOrigin eventOrigin = activityDeletedEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            log.info("根据参数:{} 处理活动删除事件任务", JSON.toJSONString(eventOrigin));
            activityDeletedEventQueueService.handle(eventOrigin);
            log.info("处理活动删除事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动删除事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityDeletedEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动删除事件任务 end");
        }
    }

}