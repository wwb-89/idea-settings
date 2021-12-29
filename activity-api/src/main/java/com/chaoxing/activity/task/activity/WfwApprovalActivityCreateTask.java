package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.wfwform.WfwApprovalActivityCreateDTO;
import com.chaoxing.activity.service.queue.activity.WfwApprovalActivityCreateQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwApprovalActivityCreateQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**通过审批创建活动任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreateFromApprovalTask
 * @description
 * @blame wwb
 * @date 2021-05-11 16:37:54
 */
@Slf4j
@Component
public class WfwApprovalActivityCreateTask {

    @Resource
    private WfwApprovalActivityCreateQueue wfwApprovalActivityCreateQueue;
    @Resource
    private WfwApprovalActivityCreateQueueService wfwApprovalActivityCreateQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理通过审批创建活动任务 start");
        WfwApprovalActivityCreateDTO formCreateActivity = wfwApprovalActivityCreateQueue.pop();
        try {
            if (formCreateActivity == null) {
                return;
            }
            log.info("根据参数:{} 处理通过审批创建活动任", JSON.toJSONString(formCreateActivity));
            wfwApprovalActivityCreateQueueService.handle(formCreateActivity.getFid(),
                    formCreateActivity.getFormId(),
                    formCreateActivity.getFormUserId(),
                    formCreateActivity.getMarketId(),
                    formCreateActivity.getFlag(),
                    formCreateActivity.getWebTemplateId());
            log.info("处理通过审批创建活动任务 success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("根据参数:{} 处理通过审批创建活动任务 error:{}", JSON.toJSONString(formCreateActivity), e);
            wfwApprovalActivityCreateQueue.delayPush(formCreateActivity);
        }finally {
            log.info("处理通过审批创建活动任务 end");
        }
    }

}
