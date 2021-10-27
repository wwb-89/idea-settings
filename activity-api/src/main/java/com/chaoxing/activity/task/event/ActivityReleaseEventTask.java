package com.chaoxing.activity.task.event;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.ActivityReleaseEventOrigin;
import com.chaoxing.activity.service.queue.event.ActivityReleaseEventQueue;
import com.chaoxing.activity.service.queue.event.handler.ActivityReleaseEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
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

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityReleaseEventOrigin eventOrigin = activityReleaseEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityReleaseEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动发布事件error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityReleaseEventQueue.push(eventOrigin);
        }
    }

}