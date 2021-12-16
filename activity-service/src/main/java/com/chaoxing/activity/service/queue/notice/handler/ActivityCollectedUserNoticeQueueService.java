package com.chaoxing.activity.service.queue.notice.handler;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.queue.notice.ActivityCollectedUserNoticeQueue;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**给收藏活动的用户发送通知
 * @author wwb
 * @version ver 1.0
 * @className ActivityCollectedUserNoticeQueueService
 * @description
 * @blame wwb
 * @date 2021-10-26 15:33:15
 */
@Slf4j
@Service
public class ActivityCollectedUserNoticeQueueService {

    @Resource
    private XxtNoticeApiService xxtNoticeApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCollectionQueryService activityCollectionQueryService;

    public void handle(ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        List<Integer> collectedUids = activityCollectionQueryService.listCollectedUid(activity.getId());
        if (CollectionUtils.isEmpty(collectedUids)) {
            return;
        }
        String title = queueParam.getTitle();
        String content = queueParam.getContent();
        String attachment = NoticeDTO.generateActivityAttachment(activity.getName(), activity.getPreviewUrl());
        xxtNoticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, collectedUids);
    }

}
