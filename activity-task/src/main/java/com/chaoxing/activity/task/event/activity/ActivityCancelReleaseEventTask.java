package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityCancelReleaseEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityCancelReleaseEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityCancelReleaseEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动取消发布事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCancelReleaseEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 15:05:45
 */
@Slf4j
@Component
public class ActivityCancelReleaseEventTask {

    @Resource
    private ActivityCancelReleaseEventQueue activityCancelReleaseEventQueue;
    @Resource
    private ActivityCancelReleaseEventQueueService activityCancelReleaseEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动取消发布事件任务 start");
        ActivityCancelReleaseEventOrigin eventOrigin = activityCancelReleaseEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动取消发布事件", JSON.toJSONString(eventOrigin));
            activityCancelReleaseEventQueueService.handle(eventOrigin);
            log.info("处理活动取消发布事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动取消发布事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityCancelReleaseEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动取消发布事件任务 end");
        }
    }

}
