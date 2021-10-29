package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityTimeChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityTimeChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动时间改变事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimeChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-28 18:28:46
 */
@Slf4j
@Component
public class ActivityTimeChangeEventTask {

    @Resource
    private ActivityTimeChangeEventQueue activityTimeChangeEventQueue;
    @Resource
    private ActivityTimeChangeEventQueueService activityTimeChangeEventQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        ActivityTimeChangeEventOrigin eventOrigin = activityTimeChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityTimeChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动时间改变事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityTimeChangeEventQueue.push(eventOrigin);
        }

    }

}
