package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityAddressTimeChangeEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.dto.notice.NoticeTemplateFieldDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.notice.SystemNoticeTemplateService;
import com.chaoxing.activity.service.queue.notice.ActivityCollectedUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.ActivitySignedUpUserNoticeQueue;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCollectedUserNoticeQueue activityCollectedUserNoticeQueue;
    @Resource
    private ActivitySignedUpUserNoticeQueue activitySignedUpUserNoticeQueue;
    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;
    @Resource
    private SystemNoticeTemplateService systemNoticeTemplateService;

    public void handle(ActivityAddressTimeChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.ACTIVITY_INFO_CHANGE.getValue());
        NoticeTemplateFieldDTO noticeTemplateField = systemNoticeTemplateService.buildNoticeField(activity);
        String title = generateTitle(activity, noticeTemplateField, noticeTemplate);
        String content = generateContent(activity, noticeTemplateField, noticeTemplate);

        noticeSignedUp(activity, title, content);
        noticeCollected(activity, title, content);
    }

    private void noticeSignedUp(Activity activity, String title, String content) {
        Integer activityId = activity.getId();
        ActivityCollectedUserNoticeQueue.QueueParamDTO activityCollectedUserNoticeQueueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activityCollectedUserNoticeQueue.push(activityCollectedUserNoticeQueueParam);
    }

    private void noticeCollected(Activity activity, String title, String content) {
        Integer activityId = activity.getId();
        ActivitySignedUpUserNoticeQueue.QueueParamDTO activitySignedUpUserNoticeQueueParam = ActivitySignedUpUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activitySignedUpUserNoticeQueue.push(activitySignedUpUserNoticeQueueParam);
    }

    private String generateTitle(Activity activity, NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate) {
        return Optional.ofNullable(noticeTemplate).map(v -> generateTemplateTitle(noticeTemplateField, v.getCodeTitle())).orElse(generateDefaultTitle(activity));
    }

    private String generateDefaultTitle(Activity activity) {
        return "活动提醒：" + activity.getName();
    }

    private String generateTemplateTitle(NoticeTemplateFieldDTO noticeTemplateField, String titleTemplate) {
        if (StringUtils.isBlank(titleTemplate)) {
            return "";
        }
        return SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(titleTemplate, noticeTemplateField);
    }

    private String generateContent(Activity activity, NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate) {
        return Optional.ofNullable(noticeTemplate).map(v -> generateTemplateContent(noticeTemplateField, v.getCodeContent())).orElse(generateDefaultContent(activity));
    }

    private String generateDefaultContent(Activity activity) {
        String content = "您好，活动信息有调整，请合理安排时间" + CommonConstant.NEW_LINE_CHAR;
        content += "活动名称："+ activity.getName() + CommonConstant.NEW_LINE_CHAR;
        // 活动地点
        String address = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        if (StringUtils.isNotBlank(address)) {
            content += "活动地点：" + address + CommonConstant.NEW_LINE_CHAR;
        }
        content += "活动时间：" + activity.getStartTime().format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER) + CommonConstant.NEW_LINE_CHAR;
        return content;
    }

    private String generateTemplateContent(NoticeTemplateFieldDTO noticeTemplateField, String contentTemplate) {
        return SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(contentTemplate, noticeTemplateField);
    }

}