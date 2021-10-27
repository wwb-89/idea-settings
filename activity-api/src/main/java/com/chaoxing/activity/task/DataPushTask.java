package com.chaoxing.activity.task;

import com.chaoxing.activity.service.data.DataPushService;
import com.chaoxing.activity.service.queue.DataPushQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**数据推送
 * @author wwb
 * @version ver 1.0
 * @className DataPushQueueTask
 * @description
 * @blame wwb
 * @date 2021-06-24 19:48:47
 */
@Slf4j
@Component
public class DataPushTask {

    @Resource
    private DataPushQueue dataPushQueueService;
    @Resource
    private DataPushService dataPushService;

    @Scheduled(fixedDelay = 1L)
    public void consumerDataPushQueue() throws InterruptedException {
        DataPushService.DataPushParamDTO dataPushParam = dataPushQueueService.pop();
        if (dataPushParam == null) {
            return;
        }
        try {
            dataPushService.handleDataPush(dataPushParam);
        } catch (Exception e) {
            e.printStackTrace();
            dataPushQueueService.push(dataPushParam);
        }
    }

}
