package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityAboutEndEventOrigin;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
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

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private XxtNoticeApiService xxtNoticeApiService;

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
        Boolean openRating = Optional.ofNullable(activity.getOpenRating()).orElse(false);
        if (!openRating) {
            return;
        }
        List<Integer> noRateSignedUpUids = activityQueryService.listNoRateSignedUpUid(activity);
        if (CollectionUtils.isEmpty(noRateSignedUpUids)) {
            return;
        }
        String title = generateActivityEndNoticeTitle(activity);
        String content = generateActivityEndNoticeContent(activity);
        String attachment = NoticeDTO.generateAttachment(title, activity.getPreviewUrl());
        xxtNoticeApiService.sendNotice(title, content, attachment, CommonConstant.NOTICE_SEND_UID, noRateSignedUpUids);
    }

    private String generateActivityEndNoticeTitle(Activity activity) {
        return "活动评价：" + activity.getName();
    }

    private String generateActivityEndNoticeContent(Activity activity) {
        return "你好，感谢参与，请给本次活动评分" + "\n" +
                "活动名称：" + activity.getName() + "\n" +
                "活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
    }

}