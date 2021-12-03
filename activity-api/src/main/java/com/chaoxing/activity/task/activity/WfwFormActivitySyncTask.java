package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.queue.activity.WfwFormSyncActivityQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**万能表单活动同步任务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivitySyncTask
 * @description
 * @blame wwb
 * @date 2021-11-29 10:58:24
 */
@Slf4j
@Component
public class WfwFormActivitySyncTask {

    @Resource
    private WfwFormSyncActivityQueue wfwFormSyncActivityQueue;
    @Resource
    private WfwFormSyncActivityQueueService wfwFormSyncActivityQueueService;
    @Resource
    private ActivityHandleService activityHandleService;

    @Scheduled(fixedDelay = 10L)
    public void handleWfwUserSignUpInfoDelete() {
        log.info("处理万能表单活动数据推送任务 start");
        ActivityCreateFromFormParamDTO queueParam = wfwFormSyncActivityQueue.pop();
        try {
            if (queueParam == null) {
                return;
            }
            log.info("根据参数:{} 处理万能表单活动数据推送任务", JSON.toJSONString(queueParam));
            ActivityCreateFromFormParamDTO.OperateTypeEnum operateTypeEnum = ActivityCreateFromFormParamDTO.OperateTypeEnum.fromValue(queueParam.getOp());
            Integer fid = queueParam.getDeptId();
            Integer formId = queueParam.getFormId();
            Integer formUserId = queueParam.getIndexID();
            Integer webTemplateId = queueParam.getWebTemplateId();
            String flag = queueParam.getFlag();
            switch (operateTypeEnum) {
                case CREATE:
                    wfwFormSyncActivityQueueService.add(fid, formId, formUserId, webTemplateId, flag);
                    break;
                case UPDATE:
                    wfwFormSyncActivityQueueService.update(fid, formId, formUserId, webTemplateId, flag);
                    break;
                case DELETE:
                    activityHandleService.deleteWfwFormActivity(formId, formUserId);
                    break;
                default:
            }
            log.info("根据参数:{} 处理万能表单活动数据推送任务 success", JSON.toJSONString(queueParam));
        } catch (Exception e) {
            log.error("根据参数:{} 处理万能表单活动数据推送任务 error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
        } finally {
            log.info("处理万能表单活动数据推送任务 end");
        }
    }

}