package com.chaoxing.activity.task;

import com.chaoxing.activity.service.ActivityStatHandleService;
import com.chaoxing.activity.service.queue.ActivityStatQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动统计任务
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/10 4:45 下午
 * <p>
 */

@Component
public class ActivityStatTask {

    @Resource
    private ActivityStatQueueService activityStatQueueService;

    @Resource
    private ActivityStatHandleService activityStatHandleService;

    /**生成活动统计任务
     * 每天凌晨十二点半执行，统计前一天的
     *
    * @Description 
    * @author huxiaolong
    * @Date 2021-05-10 16:48:39
    * @param 
    * @return void
    */
    @Scheduled(cron = "0 30 0 * * ?")
    public void generateActivityStatTask() {
        activityStatQueueService.addActivityStatTask(null);
    }

    /**处理活动统计任务
     *
    * @Description
    * @author huxiaolong
    * @Date 2021-05-10 16:48:56
    * @param
    * @return void
    */
    @Scheduled(fixedDelay = 1L)
    public void handleActivityStatTask() throws InterruptedException {
        Integer taskId = activityStatQueueService.getActivityStatTask();

        if (taskId == null) {
            return;
        }
        boolean result = false;
        try {
            result = activityStatHandleService.handleTask(taskId);
        } catch (Exception e) {

        }
        if (!result) {
            activityStatQueueService.addActivityStatTask(taskId);
        }

    }

}