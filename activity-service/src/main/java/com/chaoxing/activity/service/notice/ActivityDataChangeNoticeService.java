package com.chaoxing.activity.service.notice;

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
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataChangeNoticeService
 * @description
 * @blame wwb
 * @date 2021-05-12 19:02:54
 */
@Slf4j
@Service
public class ActivityDataChangeNoticeService {

    /** 活动时间格式化 */
    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCollectionQueryService activityCollectionQueryService;
    @Resource
    private XxtNoticeApiService xxtNoticeApiService;

    /**给收藏活动的用户发送通知
     * @Description 
     * @author wwb
     * @Date 2021-05-12 19:26:38
     * @param activityId
     * @return void
    */
    public void sendToCollected(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        List<Integer> uids = activityCollectionQueryService.listCollectedUid(activityId);
        if (CollectionUtils.isNotEmpty(uids)) {
            sendNotice(activity, uids);
        }
    }

    /**给活动报名用户发送通知
     * @Description 
     * @author wwb
     * @Date 2021-05-12 19:29:09
     * @param activityId
     * @return void
    */
    public void sendToSignedUp(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        List<Integer> uids = activityQueryService.listSignedUpUid(activity);
        if (CollectionUtils.isNotEmpty(uids)) {
            sendNotice(activity, uids);
        }
    }

    private String getActivityAddress(Activity activity) {
        String address = activity.getAddress();
        address = Optional.of(address).orElse("");
        String detailAddress = activity.getDetailAddress();
        detailAddress = Optional.of(detailAddress).orElse("");
        return address + detailAddress;
    }

    private void sendNotice(Activity activity, List<Integer> uids) {
        String title = "活动提醒：" + activity.getName();
        String content = "您好，活动信息有调整，请合理安排时间\n";
        content += "活动名称：成都市青羊区少儿阅读活动\n";
        // 活动地点
        String address = getActivityAddress(activity);
        if (StringUtils.isNotBlank(address)) {
            content += "活动地点：" + address + "\n";
        }
        content += "活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
        String attachment = NoticeDTO.generateAttachment(activity.getName(), activity.getPreviewUrl());
        Integer senderUid = CommonConstant.NOTICE_SEND_UID;
        xxtNoticeApiService.sendNotice(title, content, attachment, senderUid, uids);
    }

}
