package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityEndTimeReachEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityEndTimeReachEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityEndTimeReachEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动结束时间到达任务处理
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndTimeReachEventTask
 * @description
 * @blame wwb
 * @date 2021-10-27 11:28:18
 */
@Slf4j
@Component
public class ActivityEndTimeReachEventTask {

    @Resource
    private ActivityEndTimeReachEventQueue activityEndTimeReachEventQueue;
    @Resource
    private ActivityEndTimeReachEventQueueService activityEndTimeReachEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动结束时间到达任务处理 start");
        ActivityEndTimeReachEventOrigin eventOrigin = activityEndTimeReachEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理活动结束时间到达任务处理", JSON.toJSONString(eventOrigin));
            activityEndTimeReachEventQueueService.handle(eventOrigin);
            log.info("处理活动结束时间到达任务处理 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动结束时间到达任务处理 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
            e.printStackTrace();
            activityEndTimeReachEventQueue.rePush(eventOrigin);
        } finally {
            log.info("处理活动结束时间到达任务处理 end");
        }
    }

}
