package com.chaoxing.activity.task.event;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.ActivityAboutStartEventOrigin;
import com.chaoxing.activity.service.queue.event.handler.ActivityAboutStartEventQueueService;
import com.chaoxing.activity.service.queue.event.ActivityAboutStartEventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutStartEventTask
 * @description
 * @blame wwb
 * @date 2021-10-26 11:38:59
 */
@Slf4j
@Component
public class ActivityAboutStartEventTask {

    @Resource
    private ActivityAboutStartEventQueue activityAboutStartEventQueue;
    @Resource
    private ActivityAboutStartEventQueueService activityAboutStartEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityAboutStartEventOrigin eventOrigin = activityAboutStartEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityAboutStartEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动即将开始事件error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityAboutStartEventQueue.push(eventOrigin);
        }
    }

}