package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityAddressTimeChangeEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.notice.ActivityCollectedUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.ActivitySignedUpUserNoticeQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityAddressTimeChangeEventQueueService
 * @description 活动地点或时间改变后通知"已收藏"和"已报名"的用户
 * @blame wwb
 * @date 2021-10-27 15:47:46
 */
@Slf4j
@Service
public class ActivityAddressTimeChangeEventQueueService {

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCollectedUserNoticeQueue activityCollectedUserNoticeQueue;
    @Resource
    private ActivitySignedUpUserNoticeQueue activitySignedUpUserNoticeQueue;

    public void handle(ActivityAddressTimeChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        String title = generateTitle(activity);
        String content = generateContent(activity);
        ActivityCollectedUserNoticeQueue.QueueParamDTO activityCollectedUserNoticeQueueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activityCollectedUserNoticeQueue.push(activityCollectedUserNoticeQueueParam);
        ActivitySignedUpUserNoticeQueue.QueueParamDTO activitySignedUpUserNoticeQueueParam = ActivitySignedUpUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activitySignedUpUserNoticeQueue.push(activitySignedUpUserNoticeQueueParam);
    }

    private String generateTitle(Activity activity) {
        return "活动提醒：" + activity.getName();
    }

    private String generateContent(Activity activity) {
        String content = "您好，活动信息有调整，请合理安排时间\n";
        content += "活动名称："+ activity.getName() +"\n";
        // 活动地点
        String address = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        if (StringUtils.isNotBlank(address)) {
            content += "活动地点：" + address + "\n";
        }
        content += "活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
        return content;
    }

}