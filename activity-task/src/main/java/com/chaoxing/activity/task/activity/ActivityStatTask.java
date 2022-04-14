package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.stat.ActivityStatHandleService;
import com.chaoxing.activity.service.queue.activity.ActivityStatQueue;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class ActivityStatTask {

    @Resource
    private ActivityStatQueue activityStatQueue;

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
        log.info("生成活动统计任务 start");
        try {
            activityStatQueue.addActivityStatTask();
            log.info("生成活动统计任务 success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("生成活动统计任务 error:{}", e.getMessage());
        } finally {
            log.info("生成活动统计任务 end");
        }
    }

    /**处理活动统计任务
     *
    * @Description
    * @author huxiaolong
    * @Date 2021-05-10 16:48:56
    * @param
    * @return void
    */
    @Scheduled(fixedDelay = 10L)
    public void handleActivityStatTask() throws InterruptedException {
        log.info("处理活动统计任务 start");
        Integer taskId = activityStatQueue.popActivityStatTask();
        if (taskId == null) {
            log.info("处理活动统计任务 忽略");
            return;
        }
        log.info("根据参数:{} 处理活动统计任务", taskId);
        boolean result = false;
        try {
            result = activityStatHandleService.handleTask(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result) {
                log.info("根据参数:{} 处理活动统计任务 success", taskId);
            }else{
                log.error("根据参数:{} 处理活动统计任务 error", taskId);
                activityStatQueue.pushActivityStatTask(taskId);
            }
            log.info("处理活动统计任务 end");
        }
    }

}