package com.chaoxing.activity.task.event;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.ActivityChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.ActivityChangeEventQueue;
import com.chaoxing.activity.service.queue.event.handler.ActivityChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 15:31:20
 */
@Slf4j
@Component
public class ActivityChangeEventTask {

    @Resource
    private ActivityChangeEventQueue activityChangeEventQueue;
    @Resource
    private ActivityChangeEventQueueService activityChangeEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityChangeEventOrigin eventOrigin = activityChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityChangeEventQueue.push(eventOrigin);
        }
    }

}
