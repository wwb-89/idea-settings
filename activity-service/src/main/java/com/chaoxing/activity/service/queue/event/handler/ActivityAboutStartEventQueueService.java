package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityAboutStartEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.notice.ActivityCollectedUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.ActivitySignedUpUserNoticeQueue;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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


    public void handle(ActivityAboutStartEventOrigin eventOrigin) {
        Activity activity = activityQueryService.getById(eventOrigin.getActivityId());
        if (!needHandle(activity)) {
            return;
        }
        handleSignedUpUserNotice(activity);
        handleCollectedUserNotice(activity);
    }

    private void handleSignedUpUserNotice(Activity activity) {
        ActivitySignedUpUserNoticeQueue.QueueParamDTO queueParam = ActivitySignedUpUserNoticeQueue.QueueParamDTO.builder()
                .activityId(activity.getId())
                .title(generateSignedUpNoticeTitle(activity))
                .content(generateSignedUpNoticeContent(activity))
                .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                .build();
        activitySignedUpUserNoticeQueue.push(queueParam);
    }

    private void handleCollectedUserNotice(Activity activity) {
        Integer signId = activity.getSignId();
        List<SignUpCreateParamDTO> signUps = null;
        if (signId != null) {
            SignCreateParamDTO signCreateParam = signApiService.getCreateById(signId);
            signUps = Optional.ofNullable(signCreateParam).map(SignCreateParamDTO::getSignUps).orElse(Lists.newArrayList());
        }
        if (CollectionUtils.isNotEmpty(signUps)) {
            for (SignUpCreateParamDTO signUp : signUps) {
                ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                        .activityId(activity.getId())
                        .title(generateCollectedNoticeTitle(activity))
                        .content(generateCollectedNoticeContent(activity, signUp))
                        .attachment(NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl()))
                        .build();
                activityCollectedUserNoticeQueue.push(queueParam);
            }
        } else {
            ActivityCollectedUserNoticeQueue.QueueParamDTO queueParam = ActivityCollectedUserNoticeQueue.QueueParamDTO.builder()
                    .activityId(activity.getId())
                    .title(generateCollectedNoticeTitle(activity))
                    .content(generateCollectedNoticeContent(activity, null))
                    .build();
            activityCollectedUserNoticeQueue.push(queueParam);
        }
    }

    private boolean needHandle(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (!Objects.equals(Activity.StatusEnum.RELEASED.getValue(), activity.getStatus())) {
            return false;
        }
        long nowTimestamp = DateUtils.date2Timestamp(LocalDateTime.now());
        long startTimestamp = DateUtils.date2Timestamp(activity.getStartTime());
        if (startTimestamp - nowTimestamp < CommonConstant.ACTIVITY_BEFORE_START_NOTICE_TIME_THRESHOLD) {
            // 小于通知阈值不处理
            return false;
        }
        return true;
    }

    private String generateSignedUpNoticeTitle(Activity activity) {
        return "您报名的" + activity.getName() + "即将开始！";
    }

    private String generateSignedUpNoticeContent(Activity activity) {
        String content = "活动名称：" + activity.getName() + "\n" +
                "活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
        return content;
    }

    private String generateCollectedNoticeTitle(Activity activity) {
        return "您收藏的" + activity.getName() + "即将开始！";
    }

    private String generateCollectedNoticeContent(Activity activity, SignUpCreateParamDTO signUp) {
        String content = "活动名称：" + activity.getName() + "\n" +
                "活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
        if (signUp != null) {
            content += "报名时间：" + DateUtils.timestamp2Date(signUp.getStartTime()).format(SIGN_UP_TIME_FORMATTER) + "- " + DateUtils.timestamp2Date(signUp.getEndTime()).format(SIGN_UP_TIME_FORMATTER) + "\n";
        }
        return content;
    }

    private String generateUserSignedUpNoticeTitle(Activity activity) {
        return "成功报名活动 " + activity.getName();
    }

    private String generateUserSignedUpNoticeContent(Activity activity) {
        String content = "您好，您已成功报名活动！\n";
        content += "活动名称：" + activity.getName() + "\n";
        String address = Optional.ofNullable(activity.getAddress()).filter(StringUtils::isNotBlank).orElse("") + Optional.ofNullable(activity.getDetailAddress()).filter(StringUtils::isNotBlank).orElse("");
        if (StringUtils.isNotBlank(address)) {
            content += "活动地点：" + address + "\n";
        }
        content += "会议时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
        return content;
    }

}