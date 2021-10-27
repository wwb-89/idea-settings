package com.chaoxing.activity.service.queue.notice.handler;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.queue.notice.ActivitySignedUpUserNoticeQueue;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**给活动报名的用户发送通知服务
 * @author wwb
 * @version ver 1.0
 * @className ActivitySignedUpUserNoticeQueueService
 * @description
 * @blame wwb
 * @date 2021-10-26 15:26:41
 */
@Slf4j
@Service
public class ActivitySignedUpUserNoticeQueueService {

    @Resource
    private XxtNoticeApiService xxtNoticeApiService;
    @Resource
    private ActivityQueryService activityQueryService;

    public void handle(ActivitySignedUpUserNoticeQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        List<Integer> signedUids = activityQueryService.listSignedUpUid(activity);
        if (CollectionUtils.isEmpty(signedUids)) {
            return;
        }
        String title = queueParam.getTitle();
        String content = queueParam.getContent();
        String attachment = NoticeDTO.generateAttachment(title, activity.getPreviewUrl());
        xxtNoticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, signedUids);
    }

}