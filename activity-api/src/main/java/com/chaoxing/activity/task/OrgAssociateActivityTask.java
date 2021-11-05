package com.chaoxing.activity.task;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.activity.ActivityMarketService;
import com.chaoxing.activity.service.queue.OrgAssociateActivityQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**机构关联活动任务
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
    private OrgAssociateActivityQueue orgAssociateActivityQueueService;
    @Resource
    private ActivityMarketService activityMarketService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        OrgAssociateActivityQueue.QueueParamDTO queueParam = orgAssociateActivityQueueService.pop();
        if (queueParam == null) {
            return;
        }
        try {
            activityMarketService.orgAssociatedActivity(queueParam.getFid(), queueParam.getActivityId(), queueParam.getUid());
        } catch (Exception e) {
            log.error("根据参数:{} 处理机构关联活动任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            orgAssociateActivityQueueService.push(queueParam);
        }
    }

}