package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityAboutEndEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityAboutEndEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityAboutEndEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动即将结束任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutEndEventTask
 * @description
 * @blame wwb
 * @date 2021-10-26 19:12:11
 */
@Slf4j
@Component
public class ActivityAboutEndEventTask {

    @Resource
    private ActivityAboutEndEventQueue activityAboutEndEventQueue;
    @Resource
    private ActivityAboutEndEventQueueService activityAboutEndEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        ActivityAboutEndEventOrigin eventOrigin = activityAboutEndEventQueue.pop();
        if (eventOrigin == null) {
            return;
        }
        try {
            activityAboutEndEventQueueService.handle(eventOrigin);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动即将结束任务error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityAboutEndEventQueue.push(eventOrigin);
        }
    }

}