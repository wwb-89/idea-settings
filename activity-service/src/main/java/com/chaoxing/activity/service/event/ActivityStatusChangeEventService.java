package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.event.activity.ActivityDeletedEventOrigin;
import com.chaoxing.activity.dto.event.activity.ActivityEndEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.event.activity.ActivityDeletedEventQueue;
import com.chaoxing.activity.service.queue.event.activity.ActivityEndEventQueue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**活动状态改变事件服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusChangeEventService
 * @description
 * @blame wwb
 * @date 2021-10-27 17:32:53
 */
@Slf4j
@Service
public class ActivityStatusChangeEventService {

    @Resource
    private ActivityEndEventQueue activityEndEventQueue;
    @Resource
    private ActivityDeletedEventQueue activityDeletedEventQueue;

    /**活动状态变更
     * @Description
     * @author wwb
     * @Date 2021-03-26 19:50:31
     * @param activity
     * @param oldStatus 旧的状态
     * @return void
     */
    public void statusChange(Activity activity, Integer oldStatus) {
        // 新旧状态一致则忽略
        if (Objects.equals(activity.getStatus(), oldStatus)) {
            return;
        }
        Integer activityId = activity.getId();
        Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(activity.getStatus());
        Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
        switch (statusEnum) {
            case DELETED:
                ActivityDeletedEventOrigin activityDeletedEventOrigin = ActivityDeletedEventOrigin.builder()
                        .activityId(activityId)
                        .timestamp(timestamp)
                        .build();
                activityDeletedEventQueue.push(activityDeletedEventOrigin);
                break;
            case ENDED:
                ActivityEndEventOrigin activityEndEventOrigin = ActivityEndEventOrigin.builder()
                        .activityId(activityId)
                        .oldStatus(oldStatus)
                        .timestamp(timestamp)
                        .build();
                activityEndEventQueue.push(activityEndEventOrigin);
                break;
            default:
        }
    }

}