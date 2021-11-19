package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityAboutEndEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.dto.notice.NoticeTemplateFieldDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.notice.SystemNoticeTemplateService;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**活动即将结束的处理
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutEndEventQueueService
 * @description 通知已报名的用户去评价（如果活动开启了评价但是用户还未评价）
 * @blame wwb
 * @date 2021-10-26 19:01:59
 */
@Slf4j
@Service
public class ActivityAboutEndEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private XxtNoticeApiService xxtNoticeApiService;
    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;
    @Resource
    private SystemNoticeTemplateService systemNoticeTemplateService;

    public void handle(ActivityAboutEndEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        if (Objects.equals(Activity.StatusEnum.ENDED.getValue(), activity.getStatus())) {
            // 活动已结束，忽略
            return;
        }
        if (!Objects.equals(activity.getEndTime(), eventOrigin.getEndTime())) {
            return;
        }
        Boolean openRating = Optional.ofNullable(activity.getOpenRating()).orElse(false);
        if (!openRating) {
            return;
        }
        List<Integer> noRateSignedUpUids = activityQueryService.listNoRateSignedUpUid(activity);
        if (CollectionUtils.isEmpty(noRateSignedUpUids)) {
            return;
        }
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.ACTIVITY_ABOUT_END.getValue());
        NoticeTemplateFieldDTO noticeTemplateField = systemNoticeTemplateService.buildNoticeField(activity);

        String waitConvertTitle = generateTitle(noticeTemplateField, noticeTemplate);
        String waitConvertContent = generateContent(noticeTemplateField, noticeTemplate);
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, noticeTemplateField);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, noticeTemplateField);
        String attachment = NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl());
        xxtNoticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, noRateSignedUpUids);
    }

    private String generateTitle(NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate) {
        return Optional.ofNullable(noticeTemplate).map(MarketNoticeTemplateDTO::getCodeTitle).orElse(generateActivityEndNoticeTitle(noticeTemplateField));
    }

    private String generateContent(NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate) {
        return Optional.ofNullable(noticeTemplate).map(MarketNoticeTemplateDTO::getCodeContent).orElse(generateActivityEndNoticeContent(noticeTemplateField));
    }

    private String generateActivityEndNoticeTitle(NoticeTemplateFieldDTO noticeTemplateField) {
        return "活动评价：" + noticeTemplateField.getActivityName();
    }

    private String generateActivityEndNoticeContent(NoticeTemplateFieldDTO noticeTemplateField) {
        return "你好，感谢参与，请给本次活动评分" + CommonConstant.NEW_LINE_CHAR +
                "活动名称：" + noticeTemplateField.getActivityName() + CommonConstant.NEW_LINE_CHAR +
                "活动时间：" + noticeTemplateField.getActivityTime();
    }

}