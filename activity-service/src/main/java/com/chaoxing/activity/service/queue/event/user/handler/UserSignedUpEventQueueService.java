package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserSignedUpEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.dto.notice.NoticeTemplateFieldDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.notice.SystemNoticeTemplateService;
import com.chaoxing.activity.service.queue.IntegralPushQueue;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueue;
import com.chaoxing.activity.service.queue.activity.ClazzInteractionAddUserQueue;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueue;
import com.chaoxing.activity.service.queue.user.UserSignStatSummaryQueue;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.enums.IntegralOriginTypeEnum;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
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
    @Resource
    private IntegralPushQueue integralPushQueue;
    @Resource
    private ActivityStatSummaryQueue activityStatSummaryQueue;
    @Resource
    private UserSignStatSummaryQueue userSignStatSummaryQueue;
    @Resource
    private UserActionRecordQueue userActionRecordQueue;
    @Resource
    private SystemNoticeTemplateService systemNoticeTemplateService;
    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;
    @Resource
    private ClazzInteractionAddUserQueue clazzInteractionAddUserQueue;

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    public void handle(UserSignedUpEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer signId = eventOrigin.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity == null) {
            return;
        }
        Integer activityId = activity.getId();
        Integer uid = eventOrigin.getUid();
        // 发送通知
        signedUpNotice(activity, uid);
        // 积分推送
        integralPush(activity, uid);
        // 相应活动的统计数据需要变更
        activityStatSummaryQueue.push(activityId);
        // 用户汇总表的报名签到统计信息需要更新
        userSignStatSummaryQueue.push(new UserSignStatSummaryQueue.QueueParamDTO(uid, activityId));
        // 如果班级互动需要通知将用户加入班级
        Boolean openClazzInteraction = Optional.ofNullable(activity.getOpenClazzInteraction()).orElse(false);
        if (openClazzInteraction) {
            clazzInteractionAddUserQueue.push(new ClazzInteractionAddUserQueue.QueueParamDTO(uid, activityId));
        }
        // 记录用户行为
        UserActionRecordQueue.QueueParamDTO queueParam = new UserActionRecordQueue.QueueParamDTO(uid, activityId, UserActionTypeEnum.SIGN_UP, UserActionEnum.SIGNED_UP, String.valueOf(eventOrigin.getSignUpId()), DateUtils.timestamp2Date(eventOrigin.getTimestamp()));
        userActionRecordQueue.push(queueParam);

    }

    /**给报名成功的用户发送通知
     * @Description 
     * @author wwb
     * @Date 2021-11-01 10:09:24
     * @param activity
     * @param uid
     * @return void
    */
    private void signedUpNotice(Activity activity, Integer uid) {
        Boolean signedUpNotice = Optional.ofNullable(activity.getSignedUpNotice()).orElse(false);
        if (!signedUpNotice) {
            return;
        }
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.SIGN_UP_SUCCESS.getValue());
        NoticeTemplateFieldDTO noticeTemplateField = systemNoticeTemplateService.buildNoticeField(activity);
        String waitConvertTitle;
        String waitConvertContent;
        if (noticeTemplate == null) {
            waitConvertTitle = generateUserSignedUpNoticeTitle(noticeTemplateField);
            waitConvertContent = generateUserSignedUpNoticeContent(noticeTemplateField);
        } else {
            waitConvertTitle = Optional.ofNullable(noticeTemplate.getCodeTitle()).orElse("");
            waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse("");
        }
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, noticeTemplateField);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, noticeTemplateField);
        String attachment = NoticeDTO.generateActivityAttachment(activity.getName(), activity.getPreviewUrl());
        List<Integer> uids = Lists.newArrayList();
        uids.add(uid);
        noticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, uids);
    }

    /**积分推送
     * @Description 
     * @author wwb
     * @Date 2021-11-01 10:10:09
     * @param activity
     * @param uid
     * @return void
    */
    private void integralPush(Activity activity, Integer uid) {
        Integer activityId = activity.getId();
        String activityName = activity.getName();
        Integer createFid = activity.getCreateFid();
        IntegralPushQueue.IntegralPushDTO integralPush = new IntegralPushQueue.IntegralPushDTO(uid, createFid, IntegralOriginTypeEnum.SIGN_UP.getValue(), String.valueOf(activityId), activityName);
        integralPushQueue.push(integralPush);
    }


    private String generateUserSignedUpNoticeTitle(NoticeTemplateFieldDTO noticeTemplateField) {
        return "成功报名活动 " + noticeTemplateField.getActivityName();
    }

    private String generateUserSignedUpNoticeContent(NoticeTemplateFieldDTO noticeTemplateField) {
        String content = "您好，您已成功报名活动！" + CommonConstant.NEW_LINE_CHAR;
        content += "活动名称：" + noticeTemplateField.getActivityName() + CommonConstant.NEW_LINE_CHAR;
        if (StringUtils.isNotBlank(noticeTemplateField.getAddress())) {
            content += "活动地点：" + noticeTemplateField.getAddress() + CommonConstant.NEW_LINE_CHAR;
        }
        content += "活动时间：" + noticeTemplateField.getActivityTime();
        return content;
    }

}
