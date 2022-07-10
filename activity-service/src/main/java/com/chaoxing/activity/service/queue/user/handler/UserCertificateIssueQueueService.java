package com.chaoxing.activity.service.queue.user.handler;

import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.certificate.CertificateHandleService;
import com.chaoxing.activity.service.queue.user.UserCertificateIssueQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**发放证书队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueQueueService
 * @description
 * @blame wwb
 * @date 2021-12-16 18:01:46
 */
@Slf4j
@Service
public class UserCertificateIssueQueueService {

    @Resource
    private CertificateHandleService certificateHandleService;
    @Resource
    private ActivityQueryService activityQueryService;

    public void handle(UserCertificateIssueQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Activity activity = activityQueryService.getById(queueParam.getActivityId());
        Integer certificateTemplateId = Optional.ofNullable(activity).map(Activity::getCertificateTemplateId).orElse(null);
        if (certificateTemplateId == null) {
            return;
        }
        certificateHandleService.issueCertificate(queueParam.getUid(), queueParam.getActivityId(), OperateUserDTO.build(activity.getCreateUid(), activity.getCreateFid()));
    }

}
