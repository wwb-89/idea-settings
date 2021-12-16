package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserCertificateIssueEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.certificate.CertificateHandleService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**用户证书发放事件队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueEventQueueService
 * @description 给用户发通知
 * @blame wwb
 * @date 2021-12-16 15:50:24
 */
@Slf4j
@Service
public class UserCertificateIssueEventQueueService {

    @Resource
    private XxtNoticeApiService xxtNoticeApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private CertificateHandleService certificateHandleService;

    public void handle(UserCertificateIssueEventOrigin userCertificateIssueEventOrigin) throws UnsupportedEncodingException {
        if (userCertificateIssueEventOrigin == null) {
            return;
        }
        Integer activityId = userCertificateIssueEventOrigin.getActivityId();
        Integer uid = userCertificateIssueEventOrigin.getUid();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        String title = generateNoticeTitle(activity);
        String content = generateNoticeContent(activity);
        String url = DomainConstant.ADMIN + "/api/certificate/download?uid=" + uid + "&activityId=" + activityId;
        String attachment = NoticeDTO.generateActivityCertificateAttachment("证书", url);
        List<Integer> uids = Lists.newArrayList(uid);
        xxtNoticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, uids);
    }

    private String generateNoticeTitle(Activity activity) {
        return "活动证书通知 " + activity.getName();
    }

    private String generateNoticeContent(Activity activity) {
        String content = "您好，您在\"" + activity.getName() + "\"中获得证书，点击以下连接或在报名详情中查看";
        return content;
    }

}