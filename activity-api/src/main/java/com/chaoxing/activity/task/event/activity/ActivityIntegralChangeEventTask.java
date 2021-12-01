package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityIntegralChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityIntegralChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityIntegralChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动积分改变事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-26 16:45:38
 */
@Slf4j
@Component
public class ActivityIntegralChangeEventTask {

    @Resource
    private ActivityIntegralChangeEventQueue activityIntegralChangeEventQueue;
    @Resource
    private ActivityIntegralChangeEventQueueService activityIntegralChangeEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        ActivityIntegralChangeEventOrigin eventOrigin = activityIntegralChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityIntegralChangeEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据餐具:{} 处理活动积分变更error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityIntegralChangeEventQueue.push(eventOrigin);
        }
    }

}
