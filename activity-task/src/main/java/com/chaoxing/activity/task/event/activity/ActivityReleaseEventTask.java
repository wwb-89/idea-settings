package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityReleaseEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityReleaseEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityReleaseEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动发布事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 15:05:56
 */
@Slf4j
@Component
public class ActivityReleaseEventTask {

    @Resource
    private ActivityReleaseEventQueue activityReleaseEventQueue;
    @Resource
    private ActivityReleaseEventQueueService activityReleaseEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动发布事件任务 start");
        ActivityReleaseEventOrigin eventOrigin = activityReleaseEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动发布事件任务", JSON.toJSONString(eventOrigin));
            activityReleaseEventQueueService.handle(eventOrigin);
            log.info("处理活动发布事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动发布事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityReleaseEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动发布事件任务 end");
        }
    }

}