package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityStartTimeReachEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityStartTimeReachEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityStartTimeReachEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动开始时间到达事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartTimeReachEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 11:21:47
 */
@Slf4j
@Component
public class ActivityStartTimeReachEventTask {

    @Resource
    private ActivityStartTimeReachEventQueue activityStartTimeReachEventQueue;
    @Resource
    private ActivityStartTimeReachEventQueueService activityStartTimeReachEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动开始时间到达事件任务 start");
        ActivityStartTimeReachEventOrigin eventOrigin = activityStartTimeReachEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityStartTimeReachEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动开始时间到达事件任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityStartTimeReachEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动开始时间到达事件任务 end");
        }
    }

}
