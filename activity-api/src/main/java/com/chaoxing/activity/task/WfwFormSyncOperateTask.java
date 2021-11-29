package com.chaoxing.activity.task;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import com.chaoxing.activity.service.queue.activity.WfwFormSyncActivityQueue;
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
    private WfwFormSyncActivityQueue wfwFormSyncActivityQueue;
    @Resource
    private WfwFormSyncActivityQueueService activityFormSyncService;
    @Resource
    private ActivityHandleService activityHandleService;

    @Scheduled(fixedDelay = 1L)
    public void handleWfwUserSignUpInfoDelete() {
        ActivityCreateFromFormParamDTO queueParam = wfwFormSyncActivityQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            ActivityCreateFromFormParamDTO.OperateTypeEnum operateTypeEnum = ActivityCreateFromFormParamDTO.OperateTypeEnum.fromValue(queueParam.getOp());
            Integer fid = queueParam.getDeptId();
            Integer formId = queueParam.getFormId();
            Integer formUserId = queueParam.getIndexID();
            Integer webTemplateId = queueParam.getWebTemplateId();
            String flag = queueParam.getFlag();
            switch (operateTypeEnum) {
                case CREATE:
                    activityFormSyncService.syncCreateActivity(fid, formId, formUserId, webTemplateId, flag);
                    break;
                case UPDATE:
                    activityFormSyncService.syncUpdateActivity(fid, formId, formUserId, webTemplateId, flag);
                    break;
                case DELETE:
                    activityHandleService.deleteWfwFormActivity(formId, formUserId);
                    break;
                default:
            }
        } catch (Exception e) {
            log.error("根据参数:{} 处理万能表单新增/修改/删除数据error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
        }
    }
}
