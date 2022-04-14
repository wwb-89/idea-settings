package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityNameTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityNameTimeChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityNameTimeChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动名称事件改变事件任务
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

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动名称事件改变事件任务 start");
        ActivityNameTimeChangeEventOrigin eventOrigin = activityNameTimeChangeEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            log.info("根据参数:{} 处理活动名称事件改变事件任务", JSON.toJSONString(eventOrigin));
            activityNameTimeChangeEventQueueService.handle(eventOrigin);
            log.info("处理活动名称事件改变事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动名称事件改变事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityNameTimeChangeEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动名称事件改变事件任务 end");
        }
    }

}
