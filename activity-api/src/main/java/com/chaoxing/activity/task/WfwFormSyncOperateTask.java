package com.chaoxing.activity.task;

import com.chaoxing.activity.dto.activity.ActivityFormSyncParamDTO;
import com.chaoxing.activity.service.activity.ActivityFormSyncService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.WfwFormSynOperateQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/27 16:39
 * <p>
 */

@Slf4j
@Component
public class WfwFormSyncOperateTask {

    @Resource
    private WfwFormSynOperateQueueService wfwFormSynOperateQueueService;
    @Resource
    private ActivityFormSyncService activityFormSyncService;
    @Resource
    private ActivityHandleService activityHandleService;

    @Scheduled(fixedDelay = 1L)
    public void handleWfwUserSignUpInfoDelete() {
        ActivityFormSyncParamDTO queueParam = wfwFormSynOperateQueueService.getActivityFormSyncOperateTask();
        if (queueParam == null) {
            return;
        }
        try {
            ActivityFormSyncParamDTO.OperateTypeEnum operateTypeEnum = ActivityFormSyncParamDTO.OperateTypeEnum.fromValue(queueParam.getOp());
            Integer fid = queueParam.getDeptId();
            Integer formId = queueParam.getFormId();
            Integer formUserId = queueParam.getIndexID();
            Integer webTemplateId = queueParam.getWebTemplateId();
            switch (operateTypeEnum) {
                case CREATE:
                    activityFormSyncService.syncCreateActivity(fid, formId, formUserId, webTemplateId);
                    break;
                case UPDATE:
                    activityFormSyncService.syncUpdateActivity(fid, formId, formUserId, webTemplateId);
                    break;
                case DELETE:
                    activityHandleService.deleteByOriginAndFormUserId(formId, formUserId);
                    break;
                default:
            }
        } catch (Exception e) {
            wfwFormSynOperateQueueService.addActivityFormSyncOperateTask(queueParam);
            e.printStackTrace();
        }
    }
}
