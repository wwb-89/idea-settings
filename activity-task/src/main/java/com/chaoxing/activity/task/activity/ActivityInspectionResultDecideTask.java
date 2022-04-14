package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueue;
import com.chaoxing.activity.service.user.result.UserResultHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/** 活动考核结果计算任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityInspectionResultDecideQueueTask
 * @description
 * @blame wwb
 * @date 2021-06-25 15:41:34
 */
@Slf4j
@Component
public class ActivityInspectionResultDecideTask {

    @Resource
    private ActivityInspectionResultDecideQueue activityInspectionResultDecideQueue;
    @Resource
    private UserResultHandleService userResultHandleService;

    @Scheduled(fixedDelay = 10L)
    public void consumerActivityInspectionResultDecideQueue() throws InterruptedException {
        log.info("处理活动考核结果计算任务 start");
        Integer activityId = activityInspectionResultDecideQueue.pop();
        try {
            if (activityId == null) {
                return;
            }
            log.info("根据参数:{} 处理活动考核结果计算任务", activityId);
            userResultHandleService.qualifiedAutoDecide(activityId);
            log.info("处理活动考核结果计算任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动考核结果计算任务 error:{}", activityId, e.getMessage());
            e.printStackTrace();
            activityInspectionResultDecideQueue.push(activityId);
        } finally {
            log.info("处理活动考核结果计算任务 end");
        }
    }

}