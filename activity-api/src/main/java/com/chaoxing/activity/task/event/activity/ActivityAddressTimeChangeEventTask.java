package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityAddressTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityAddressTimeChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityAddressTimeChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityAddressTimeChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 15:48:16
 */
@Slf4j
@Component
public class ActivityAddressTimeChangeEventTask {

    @Resource
    private ActivityAddressTimeChangeEventQueue activityAddressTimeChangeEventQueue;
    @Resource
    private ActivityAddressTimeChangeEventQueueService activityAddressTimeChangeEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityAddressTimeChangeEventOrigin eventOrigin = activityAddressTimeChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityAddressTimeChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动地点时间改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityAddressTimeChangeEventQueue.push(eventOrigin);
        }
    }

}
