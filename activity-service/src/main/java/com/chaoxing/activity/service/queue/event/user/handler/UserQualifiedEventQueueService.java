package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserQualifiedEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.InspectionConfig;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueue;
import com.chaoxing.activity.service.queue.user.UserCertificateIssueQueue;
import com.chaoxing.activity.service.queue.user.UserResultStatSummaryQueue;
import com.chaoxing.activity.service.queue.user.UserSignStatSummaryQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserQualifiedEventQueueService
 * @description
 * @blame wwb
 * @date 2021-11-01 10:49:15
 */
@Slf4j
@Service
public class UserQualifiedEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityStatSummaryQueue activityStatSummaryQueue;
    @Resource
    private UserSignStatSummaryQueue userSignStatSummaryQueue;
    @Resource
    private UserResultStatSummaryQueue userResultStatSummaryQueue;
    @Resource
    private UserCertificateIssueQueue userCertificateIssueQueue;
    @Resource
    private InspectionConfigQueryService inspectionConfigQueryService;

    public void handle(UserQualifiedEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer uid = eventOrigin.getUid();
        UserResultStatSummaryQueue.QueueParamDTO queueParam = new UserResultStatSummaryQueue.QueueParamDTO(uid, activityId);
        userResultStatSummaryQueue.push(queueParam);
        // 相应活动的统计数据需要变更
        activityStatSummaryQueue.push(activityId);
        // 用户汇总表的报名签到统计信息需要更新
        userSignStatSummaryQueue.push(new UserSignStatSummaryQueue.QueueParamDTO(uid, activityId));
        // 发放证书（如果开启了自动发放证书）
        InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
        Boolean autoIssueCertificate = Optional.ofNullable(inspectionConfig).map(InspectionConfig::getAutoIssueCertificate).orElse(false);
        if (autoIssueCertificate && activity.getCertificateTemplateId() != null) {
            userCertificateIssueQueue.push(new UserCertificateIssueQueue.QueueParamDTO(uid, activityId));
        }

    }

}