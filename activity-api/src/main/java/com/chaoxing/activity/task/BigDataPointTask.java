package com.chaoxing.activity.task;

import com.chaoxing.activity.service.data.BigDataPointTaskHandleService;
import com.chaoxing.activity.service.queue.BigDataPointQueueService;
import com.chaoxing.activity.service.queue.BigDataPointTaskQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**大数据积分任务
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointTask
 * @description
 * @blame wwb
 * @date 2021-10-13 13:59:12
 */
@Slf4j
@Component
public class BigDataPointTask {

    @Resource
    private BigDataPointTaskQueueService bigDataPointTaskQueueService;
    @Resource
    private BigDataPointQueueService bigDataPointQueueService;

    @Resource
    private BigDataPointTaskHandleService bigDataPointTaskHandleService;

    /**处理大数据积分任务
     * @Description 
     * @author wwb
     * @Date 2021-10-13 14:01:38
     * @param 
     * @return void
    */
    @Scheduled(fixedDelay = 1L)
    public void handleTask() throws InterruptedException {
        BigDataPointTaskQueueService.QueueParamDTO queueParam = bigDataPointTaskQueueService.pop();
        if (queueParam == null) {
            return;
        }
        try {
            bigDataPointTaskHandleService.handleTask(queueParam);
        } catch (Exception e) {
            e.printStackTrace();
            bigDataPointTaskQueueService.push(queueParam);
        }
    }

    /**处理积分推送任务
     * @Description 
     * @author wwb
     * @Date 2021-10-13 14:01:47
     * @param 
     * @return void
    */
    @Scheduled(fixedDelay = 1L)
    public void handleDataPush() throws InterruptedException {
        BigDataPointQueueService.QueueParamDTO queueParam = bigDataPointQueueService.pop();
        if (queueParam == null) {
            return;
        }
        try {
            bigDataPointTaskHandleService.dataPush(queueParam);
        } catch (Exception e) {
            e.printStackTrace();
            bigDataPointQueueService.push(queueParam);
        }
    }

}