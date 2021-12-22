package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateActivity;
import com.chaoxing.activity.service.manager.wfw.WfwFormApprovalApiService;
import com.chaoxing.activity.service.queue.activity.FormActivityCreateQueue;
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
public class ActivityCreateFromApprovalTask {

    @Resource
    private FormActivityCreateQueue formActivityCreateQueueService;
    @Resource
    private WfwFormApprovalApiService formApprovalApiService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理通过审批创建活动任务 start");
        WfwFormCreateActivity formCreateActivity = formActivityCreateQueueService.pop();
        try {
            if (formCreateActivity == null) {
                return;
            }
            log.info("根据参数:{} 处理通过审批创建活动任", JSON.toJSONString(formCreateActivity));
            formApprovalApiService.createActivity(formCreateActivity.getFid(),
                    formCreateActivity.getFormId(),
                    formCreateActivity.getFormUserId(),
                    formCreateActivity.getMarketId(),
                    formCreateActivity.getFlag(),
                    formCreateActivity.getWebTemplateId());
            log.info("处理通过审批创建活动任务 success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("根据参数:{} 处理通过审批创建活动任务 error:{}", JSON.toJSONString(formCreateActivity), e);
            formActivityCreateQueueService.delayPush(formCreateActivity);
        }finally {
            log.info("处理通过审批创建活动任务 end");
        }
    }

}
