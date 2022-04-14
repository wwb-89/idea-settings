package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityAddressTimeChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityAddressTimeChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityAddressTimeChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动地址时间改变事件任务
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

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动地址时间改变事件任务 start");
        ActivityAddressTimeChangeEventOrigin eventOrigin = activityAddressTimeChangeEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动地址时间改变事件任务", JSON.toJSONString(eventOrigin));
            activityAddressTimeChangeEventQueueService.handle(eventOrigin);
            log.info("处理活动地址时间改变事件任务 end");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动地址时间改变事件任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityAddressTimeChangeEventQueue.push(eventOrigin);
        } finally {
            log.info("处理活动地址时间改变事件任务 end");
        }
    }

}
