package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.MarketActivityDataPrePushQueue;
import com.chaoxing.activity.service.queue.activity.handler.MarketActivityDataPrePushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动市场活动数据预推送任务
 * @author wwb
 * @version ver 1.0
 * @className MarketActivityDataPrePushTask
 * @description 触发活动推送，将分发到活动配置的所有需要推送的目的地队列任务
 * @blame wwb
 * @date 2021-11-01 15:02:31
 */
@Slf4j
@Component
public class MarketActivityDataPrePushTask {

    @Resource
    private MarketActivityDataPrePushQueue activityDataPrePushQueue;
    @Resource
    private MarketActivityDataPrePushQueueService activityDataPrePushQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动市场活动数据预推送任务 start");
        Integer activityId = activityDataPrePushQueue.pop();
        try {
            if (activityId == null) {
                return;
            }
            log.info("根据参数:{} 处理活动市场活动数据预推送任务", activityId);
            activityDataPrePushQueueService.handle(activityId);
            log.info("处理活动市场活动数据预推送任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动市场活动数据预推送任务 error:{}", activityId, e.getMessage());
            e.printStackTrace();
            activityDataPrePushQueue.push(activityId);
        } finally {
            log.info("处理活动市场活动数据预推送任务 end");
        }
    }

}