package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityEndEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityEndEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityEndEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动结束事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 15:18:16
 */
@Slf4j
@Component
public class ActivityEndEventTask {

    @Resource
    private ActivityEndEventQueue activityEndEventQueue;
    @Resource
    private ActivityEndEventQueueService activityEndEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动结束事件任务 start");
        ActivityEndEventOrigin eventOrigin = activityEndEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动结束事件任务", JSON.toJSONString(eventOrigin));
            activityEndEventQueueService.handle(eventOrigin);
            log.info("处理活动结束事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动结束事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityEndEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动结束事件任务 end");
        }
    }

}