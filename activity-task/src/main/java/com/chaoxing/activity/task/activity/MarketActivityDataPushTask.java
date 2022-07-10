package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
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

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动市场活动数据推送任务 start");
        MarketActivityDataPushQueue.QueueParamDTO queueParam = marketActivityDataPushQueue.pop();
        try {
            if (queueParam == null) {
                return;
            }
            log.info("根据参数:{} 处理活动市场活动数据推送任务", JSON.toJSONString(queueParam));
            activityDataPushQueueService.handle(queueParam);
            log.info("处理活动市场活动数据推送任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动市场活动数据推送任务 error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            marketActivityDataPushQueue.delayPush(queueParam);
        } finally {
            log.info("处理活动市场活动数据推送任务 end");
        }
    }

}