package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityCoverChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityCoverChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityCoverChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动封面改变事件任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverChangeEventTask
 * @description
 * @blame wwb
 * @date 2021-10-26 18:24:58
 */
@Slf4j
@Component
public class ActivityCoverChangeEventTask {

    @Resource
    private ActivityCoverChangeEventQueue activityCoverChangeEventQueue;
    @Resource
    private ActivityCoverChangeEventQueueService activityCoverChangeEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动封面改变事件任务 start");
        ActivityCoverChangeEventOrigin eventOrigin = activityCoverChangeEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动封面改变事件任务", JSON.toJSONString(eventOrigin));
            activityCoverChangeEventQueueService.handle(eventOrigin);
            log.info("处理活动封面改变事件任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动封面改变事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityCoverChangeEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动封面改变事件任务 end");
        }
    }

}
