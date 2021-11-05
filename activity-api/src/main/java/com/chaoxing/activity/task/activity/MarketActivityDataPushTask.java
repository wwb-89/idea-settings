package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.MarketActivityDataPushQueue;
import com.chaoxing.activity.service.queue.activity.handler.MarketActivityDataPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动市场活动数据推送任务
 * @author wwb
 * @version ver 1.0
 * @className MarketActivityDataPushTask
 * @description
 * @blame wwb
 * @date 2021-11-01 15:05:19
 */
@Slf4j
@Component
public class MarketActivityDataPushTask {

    @Resource
    private MarketActivityDataPushQueue marketActivityDataPushQueue;
    @Resource
    private MarketActivityDataPushQueueService activityDataPushQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        MarketActivityDataPushQueue.QueueParamDTO queueParam = marketActivityDataPushQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            activityDataPushQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动数据推送任务error:{}", queueParam, e.getMessage());
            e.printStackTrace();
            marketActivityDataPushQueue.push(queueParam);
        }
    }

}