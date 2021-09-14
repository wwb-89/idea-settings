package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityMarketService;
import com.chaoxing.activity.service.queue.OrgAssociateActivityQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgAssociateActivityTask
 * @description
 * @blame wwb
 * @date 2021-09-14 15:22:26
 */
@Slf4j
@Component
public class OrgAssociateActivityTask {

    @Resource
    private OrgAssociateActivityQueueService orgAssociateActivityQueueService;
    @Resource
    private ActivityMarketService activityMarketService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        OrgAssociateActivityQueueService.QueueParamDTO queueParamDto = orgAssociateActivityQueueService.pop();
        if (queueParamDto == null) {
            return;
        }
        try {
            activityMarketService.orgAssociatedActivity(queueParamDto.getFid(), queueParamDto.getActivityId(), queueParamDto.getUid());
        } catch (Exception e) {
            e.printStackTrace();
            orgAssociateActivityQueueService.push(queueParamDto);
        }
    }

}