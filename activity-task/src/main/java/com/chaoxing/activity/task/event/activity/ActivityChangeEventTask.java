package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动改变事件任务
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

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动改变事件任务 start");
        ActivityChangeEventOrigin eventOrigin = activityChangeEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动改变事件任务", JSON.toJSONString(eventOrigin));
            activityChangeEventQueueService.handle(eventOrigin);
            log.info("处理活动改变事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动改变事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityChangeEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动改变事件任务 end");
        }
    }

}
