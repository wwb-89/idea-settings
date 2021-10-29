package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserSignedUpEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserSignedUpEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-28 18:21:28
 */
@Slf4j
@Service
public class UserSignedUpEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private XxtNoticeApiService noticeApiService;

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    public void handle(UserSignedUpEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Boolean signedUpNotice = Optional.ofNullable(activity.getSignedUpNotice()).orElse(false);
        if (!signedUpNotice) {
            return;
        }
        String title = generateUserSignedUpNoticeTitle(activity);
        String content = generateUserSignedUpNoticeContent(activity);
        String attachment = NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl());
        List<Integer> uids = Lists.newArrayList();
        uids.add(eventOrigin.getUid());
        noticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, uids);
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
