package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueue;
import com.chaoxing.activity.service.user.result.UserResultHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
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
        Integer activityId = activityInspectionResultDecideQueue.pop();
        if (activityId == null) {
            return;
        }
        try {
            userResultHandleService.qualifiedAutoDecide(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            activityInspectionResultDecideQueue.push(activityId);
        }
    }

}