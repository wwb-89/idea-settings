package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityAboutStartEventOrigin;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**活动即将开始队列处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutStartEventQueueService
 * @description 活动即将开始：给已报名的和已收藏的用户发送通知
 * @blame wwb
 * @date 2021-10-26 11:44:09
 */
@Slf4j
@Service
public class ActivityAboutStartEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SystemNoticeTemplateService systemNoticeTemplateService;

    @Resource
    private ActivitySignedUpUserNoticeQueue activitySignedUpUserNoticeQueue;
    @Resource
    private ActivityCollectedUserNoticeQueue activityCollectedUserNoticeQueue;
    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;


    public void handle(ActivityAboutStartEventOrigin eventOrigin) {
        Activity activity = activityQueryService.getById(eventOrigin.getActivityId());
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.ACTIVITY_ABOUT_START.getValue());
        if (!needHandle(eventOrigin.getStartTime(), activity, noticeTemplate)) {
            return;
        }
        NoticeTemplateFieldDTO noticeTemplateField = systemNoticeTemplateService.buildNoticeField(activity);
        handleSignedUpUserNotice(activity, noticeTemplateField, noticeTemplate);
        handleCollectedUserNotice(activity, noticeTemplateField, noticeTemplate);
    }

    private void handleSignedUpUserNotice(Activity activity, NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate) {
        String waitConvertTitle;
        String waitConvertContent;
        if (noticeTemplate == null) {
            waitConvertTitle = generateSignedUpNoticeTitle(noticeTemplateField);
            waitConvertContent = generateSignedUpNoticeContent(noticeTemplateField);
        } else {
            waitConvertTitle = Optional.ofNullable(noticeTemplate.getCodeTitle()).orElse("");
            waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse("");
        }
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, noticeTemplateField);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, noticeTemplateField);
        ActivitySignedUpUserNoticeQueue.QueueParamDTO queueParam = ActivitySignedUpUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activity.getId())
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateActivityAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activitySignedUpUserNoticeQueue.push(queueParam);
    }

    private void handleCollectedUserNotice(Activity activity, NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate) {
        String waitConvertTitle;
        String waitConvertContent;
        if (noticeTemplate == null) {
            waitConvertTitle = generateCollectedNoticeTitle(noticeTemplateField);
            waitConvertContent = generateCollectedNoticeContent(noticeTemplateField);
        } else {
            waitConvertTitle = Optional.ofNullable(noticeTemplate.getCodeTitle()).orElse("");
            waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse("");
        }
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, noticeTemplateField);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, noticeTemplateField);
        ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activity.getId())
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateActivityAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activityCollectedUserNoticeQueue.push(queueParam);
    }

    private boolean needHandle(LocalDateTime activityStartTime, Activity activity, MarketNoticeTemplateDTO noticeTemplate) {
        if (activity == null) {
            return false;
        }
        if (!Objects.equals(Activity.StatusEnum.RELEASED.getValue(), activity.getStatus())) {
            return false;
        }
        if (!noticeTemplate.getSupportTimeConfig()) {
            return true;
        }
        return Objects.equals(activity.getStartTime(), activityStartTime);
    }

    private String generateSignedUpNoticeTitle(NoticeTemplateFieldDTO noticeTemplateField) {
        return "您报名的" + noticeTemplateField.getActivityName() + "即将开始！";
    }

    private String generateSignedUpNoticeContent(NoticeTemplateFieldDTO noticeTemplateField) {
        return "活动名称：" + noticeTemplateField.getActivityName() + CommonConstant.NEW_LINE_CHAR +
                "活动时间：" + noticeTemplateField.getActivityTime() + CommonConstant.NEW_LINE_CHAR;
    }

    private String generateCollectedNoticeTitle(NoticeTemplateFieldDTO noticeTemplateField) {
        return "您收藏的" + noticeTemplateField.getActivityName() + "即将开始！";
    }

    private String generateCollectedNoticeContent(NoticeTemplateFieldDTO noticeTemplateField) {
        String content = "活动名称：" + noticeTemplateField.getActivityName() + CommonConstant.NEW_LINE_CHAR + "活动时间：" + noticeTemplateField.getActivityTime() + CommonConstant.NEW_LINE_CHAR;
        List<NoticeTemplateFieldDTO.SignUpNoticeTemplateFieldDTO> signUps = noticeTemplateField.getSignUps();
        if (CollectionUtils.isNotEmpty(signUps)) {
            content += "报名时间：" + signUps.get(0).getTime();
        }
        return content;
    }

}