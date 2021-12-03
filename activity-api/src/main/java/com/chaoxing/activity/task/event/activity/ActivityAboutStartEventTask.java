package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityAboutStartEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityAboutStartEventQueueService;
import com.chaoxing.activity.service.queue.event.activity.ActivityAboutStartEventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动即将开始任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutStartEventTask
 * @description
 * @blame wwb
 * @date 2021-10-26 11:38:59
 */
@Slf4j
@Component
public class ActivityAboutStartEventTask {

    @Resource
    private ActivityAboutStartEventQueue activityAboutStartEventQueue;
    @Resource
    private ActivityAboutStartEventQueueService activityAboutStartEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动即将开始任务 start");
        ActivityAboutStartEventOrigin eventOrigin = activityAboutStartEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动即将开始任务", JSON.toJSONString(eventOrigin));
            activityAboutStartEventQueueService.handle(eventOrigin);
            log.info("处理活动即将开始任务 end");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动即将开始任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityAboutStartEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动即将开始任务 end");
        }
    }

}