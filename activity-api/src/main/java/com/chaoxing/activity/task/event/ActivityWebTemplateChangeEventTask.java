package com.chaoxing.activity.task.event;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.ActivityWebTemplateChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.ActivityWebTemplateChangeEventQueue;
import com.chaoxing.activity.service.queue.event.handler.ActivityWebTemplateChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityWebTemplateChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 18:46:14
 */
@Slf4j
@Component
public class ActivityWebTemplateChangeEventTask {

    @Resource
    private ActivityWebTemplateChangeEventQueue activityWebTemplateChangeEventQueue;
    @Resource
    private ActivityWebTemplateChangeEventQueueService activityWebTemplateChangeEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityWebTemplateChangeEventOrigin eventOrigin = activityWebTemplateChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityWebTemplateChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动门户模版改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityWebTemplateChangeEventQueue.push(eventOrigin);
        }
    }

}
