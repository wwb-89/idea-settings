package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityEndEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityEndEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityEndEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
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
        ActivityEndEventOrigin eventOrigin = activityEndEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityEndEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动结束事件error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityEndEventQueue.push(eventOrigin);
        }
    }

}