package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityNameChangedEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityNameChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityNameChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-26 18:34:17
 */
@Slf4j
@Component
public class ActivityNameChangeEventTask {

    @Resource
    private ActivityNameChangeEventQueue activityNameChangeEventQueue;
    @Resource
    private ActivityNameChangeEventQueueService activityNameChangeEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        ActivityNameChangedEventOrigin eventOrigin = activityNameChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityNameChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动名称改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
        }
    }

}
