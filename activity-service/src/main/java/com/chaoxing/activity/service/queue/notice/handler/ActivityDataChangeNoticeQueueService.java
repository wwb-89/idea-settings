package com.chaoxing.activity.service.queue.notice.handler;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionQueryService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**活动数据改变通知队列处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataChangeNoticeQueueService
 * @description
 * @blame wwb
 * @date 2021-10-26 17:02:34
 */
@Slf4j
@Service
public class ActivityDataChangeNoticeQueueService {

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCollectionQueryService activityCollectionQueryService;
    @Resource
    private XxtNoticeApiService xxtNoticeApiService;

    public void handle(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        if (Objects.equals(Activity.StatusEnum.DELETED.getValue(), activity.getStatus())) {
            // 活动被删除
            return;
        }
        String title = generateTitle(activity);
        String content = generateContent(activity);
        noticeSignedUpUser(activity, title, content);
        noticeCollectedUser(activity, title, content);
    }

    private void noticeSignedUpUser(Activity activity, String title, String content) {
        List<Integer> signedUids = activityQueryService.listSignedUpUid(activity);
        if (CollectionUtils.isEmpty(signedUids)) {
            return;
        }
        xxtNoticeApiService.sendNotice(title, content, NoticeDTO.generateAttachment(title, activity.getPreviewUrl()), CommonConstant.NOTICE_SEND_UID, signedUids);
    }

    private void noticeCollectedUser(Activity activity, String title, String content) {
        List<Integer> collectedUids = activityCollectionQueryService.listCollectedUid(activity.getId());
        if (CollectionUtils.isEmpty(collectedUids)) {
            return;
        }
        xxtNoticeApiService.sendNotice(title, content, NoticeDTO.generateAttachment(title, activity.getPreviewUrl()), CommonConstant.NOTICE_SEND_UID, collectedUids);
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