package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityAboutStartEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.queue.notice.ActivityCollectedUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.ActivitySignedUpUserNoticeQueue;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
    /** 报名时间格式化 */
    private static final DateTimeFormatter SIGN_UP_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SignApiService signApiService;

    @Resource
    private ActivitySignedUpUserNoticeQueue activitySignedUpUserNoticeQueue;
    @Resource
    private ActivityCollectedUserNoticeQueue activityCollectedUserNoticeQueue;
    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;


    public void handle(ActivityAboutStartEventOrigin eventOrigin) {
        Activity activity = activityQueryService.getById(eventOrigin.getActivityId());
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.ACTIVITY_ABOUT_START.getValue());
        if (needHandle(activity, noticeTemplate)) {
            return;
        }
        Integer signId = activity.getSignId();
        List<SignUpCreateParamDTO> signUps = Lists.newArrayList();
        if (signId != null) {
            SignCreateParamDTO signCreateParam = signApiService.getCreateById(signId);
            signUps = Optional.ofNullable(signCreateParam).map(SignCreateParamDTO::getSignUps).orElse(Lists.newArrayList());
        }
        String activityTime = getFormatTimeScope(activity.getStartTime(), activity.getEndTime(), ACTIVITY_TIME_FORMATTER);
        handleSignedUpUserNotice(activity, activityTime, signUps, noticeTemplate);
        handleCollectedUserNotice(activity, activityTime, signUps, noticeTemplate);
    }

    private void handleSignedUpUserNotice(Activity activity, String activityTime, List<SignUpCreateParamDTO> signUps, MarketNoticeTemplateDTO noticeTemplate) {
        String waitConvertTitle = Optional.ofNullable(noticeTemplate.getCodeTitle()).orElse(generateSignedUpNoticeTitle(activity));
        String waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse(generateSignedUpNoticeContent(activity));
        SignUpCreateParamDTO signUp = signUps.stream().findFirst().orElse(null);
        String signUpTime = Optional.ofNullable(signUp).map(v -> getFormatTimeScope(v.getStartTime(), v.getEndTime(), SIGN_UP_TIME_FORMATTER)).orElse("");
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, activity.getName(), activity.getActivityFullAddress(), activityTime, signUpTime);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, activity.getName(), activity.getActivityFullAddress(), activityTime, signUpTime);
        ActivitySignedUpUserNoticeQueue.QueueParamDTO queueParam = ActivitySignedUpUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activity.getId())
                .title(title)
                .content(content)
                .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activitySignedUpUserNoticeQueue.push(queueParam);
    }

    private void handleCollectedUserNotice(Activity activity, String activityTime, List<SignUpCreateParamDTO> signUps, MarketNoticeTemplateDTO noticeTemplate) {
        String waitConvertTitle = Optional.ofNullable(noticeTemplate.getCodeTitle()).orElse(generateCollectedNoticeTitle(activity));
        if (CollectionUtils.isNotEmpty(signUps)) {
            for (SignUpCreateParamDTO signUp : signUps) {
                String waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse(generateCollectedNoticeContent(activity, signUp));
                String signUpTime = getFormatTimeScope(signUp.getStartTime(), signUp.getEndTime(), SIGN_UP_TIME_FORMATTER);
                String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, activity.getName(), activity.getActivityFullAddress(), activityTime, signUpTime);
                String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, activity.getName(), activity.getActivityFullAddress(), activityTime, signUpTime);
                ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                        .activityId(activity.getId())
                        .title(title)
                        .content(content)
                        .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                        .build();
                activityCollectedUserNoticeQueue.push(queueParam);
            }
            return;
        }
        // 无报名的情况
        String waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse(generateCollectedNoticeContent(activity, null));
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, activity.getName(), activity.getActivityFullAddress(), activityTime);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, activity.getName(), activity.getActivityFullAddress(), activityTime);
        ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activity.getId())
                .title(title)
                .content(content)
                .build();
        activityCollectedUserNoticeQueue.push(queueParam);
    }

    private boolean needHandle(Activity activity, MarketNoticeTemplateDTO noticeTemplate) {
        if (activity == null) {
            return false;
        }
        if (!Objects.equals(Activity.StatusEnum.RELEASED.getValue(), activity.getStatus())) {
            return false;
        }
        if (!noticeTemplate.getSupportTimeConfig()) {
            return true;
        }
        long nowTimestamp = DateUtils.date2Timestamp(LocalDateTime.now());
        long startTimestamp = DateUtils.date2Timestamp(activity.getStartTime());
        long delayTimeThreshold = noticeTemplate.getDelayTimeThreshold();
        // 小于通知阈值不处理
        return startTimestamp - nowTimestamp >= delayTimeThreshold;
    }

    private String generateSignedUpNoticeTitle(Activity activity) {
        return "您报名的" + activity.getName() + "即将开始！";
    }

    private String generateSignedUpNoticeContent(Activity activity) {
        return "活动名称：" + activity.getName() + "\n" +
                "活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
    }

    private String generateCollectedNoticeTitle(Activity activity) {
        return "您收藏的" + activity.getName() + "即将开始！";
    }


    private String getFormatTimeScope(Long startTimeStamp, Long endTimeStamp, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime startTime = Optional.ofNullable(startTimeStamp).map(DateUtils::timestamp2Date).orElse(null);
        LocalDateTime endTime = Optional.ofNullable(endTimeStamp).map(DateUtils::timestamp2Date).orElse(null);
        return getFormatTimeScope(startTime, endTime, dateTimeFormatter);
    }
    private String getFormatTimeScope(LocalDateTime startTime, LocalDateTime endTime, DateTimeFormatter dateTimeFormatter) {
        if (startTime == null && endTime == null) {
            return "";
        }
        if (startTime == null) {
            return endTime.format(dateTimeFormatter);
        }
        if (endTime == null) {
            return startTime.format(dateTimeFormatter);
        }
        return startTime.format(dateTimeFormatter) + "- " + endTime.format(dateTimeFormatter);
    }

    private String generateCollectedNoticeContent(Activity activity, SignUpCreateParamDTO signUp) {
        String content = "活动名称：" + activity.getName() + "\n" + "活动时间：" + getFormatTimeScope(activity.getStartTime(), activity.getEndTime(), ACTIVITY_TIME_FORMATTER) + "\n";
        if (signUp != null) {
            content += "报名时间：" + getFormatTimeScope(signUp.getStartTime(), signUp.getEndTime(), ACTIVITY_TIME_FORMATTER) + "\n";
        }
        return content;
    }

}