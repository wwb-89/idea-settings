package com.chaoxing.activity.task.event;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.ActivityNameTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.ActivityNameTimeChangeEventQueue;
import com.chaoxing.activity.service.queue.event.handler.ActivityNameTimeChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameTimeChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 15:38:22
 */
@Slf4j
@Component
public class ActivityNameTimeChangeEventTask {

    @Resource
    private ActivityNameTimeChangeEventQueue activityNameTimeChangeEventQueue;
    @Resource
    private ActivityNameTimeChangeEventQueueService activityNameTimeChangeEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityNameTimeChangeEventOrigin eventOrigin = activityNameTimeChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityNameTimeChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动名称时间改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityNameTimeChangeEventQueue.push(eventOrigin);
        }
    }

}
