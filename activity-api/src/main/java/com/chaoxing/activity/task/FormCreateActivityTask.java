package com.chaoxing.activity.task;

import com.chaoxing.activity.dto.manager.form.FormCreateActivity;
import com.chaoxing.activity.service.manager.FormApprovalApiService;
import com.chaoxing.activity.service.queue.FormActivityCreateQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormCreateActivityTask
 * @description
 * @blame wwb
 * @date 2021-05-11 16:37:54
 */
@Slf4j
@Component
public class FormCreateActivityTask {

    @Resource
    private FormActivityCreateQueueService formActivityCreateQueueService;
    @Resource
    private FormApprovalApiService formApprovalApiService;

    @Scheduled(fixedDelay = 1000L)
    public void handle() {
        FormCreateActivity formCreateActivity = formActivityCreateQueueService.get();
        if (formCreateActivity == null) {
            return;
        }
        try {
            formApprovalApiService.addActivity(formCreateActivity.getFid(),
                    formCreateActivity.getFormId(),
                    formCreateActivity.getFormUserId(),
                    formCreateActivity.getFlag());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("根据表单信息创建活动error:{}", e);
            formActivityCreateQueueService.add(formCreateActivity);
        }
    }

}
