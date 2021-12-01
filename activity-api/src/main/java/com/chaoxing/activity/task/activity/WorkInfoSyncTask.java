package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.WorkInfoSyncQueue;
import com.chaoxing.activity.service.queue.activity.handler.WorkInfoSyncQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**作品征集信息同步任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityWorkInfoSyncTask
 * @description
 * @blame wwb
 * @date 2021-09-13 15:38:03
 */
@Slf4j
@Component
public class WorkInfoSyncTask {

    @Resource
    private WorkInfoSyncQueue workInfoSyncQueue;
    @Resource
    private WorkInfoSyncQueueService workInfoSyncQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        Integer activityId = workInfoSyncQueue.pop();
        try {
            workInfoSyncQueueService.handle(activityId);
        } catch (Exception e) {
            log.error("根据参数:{} 处理作品征集信息同步error:{}", activityId, e.getMessage());
            e.printStackTrace();
            workInfoSyncQueue.delayPush(activityId);
        }
    }

}
