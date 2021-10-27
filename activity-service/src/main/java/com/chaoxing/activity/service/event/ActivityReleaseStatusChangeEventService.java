package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.event.ActivityCancelReleaseEventOrigin;
import com.chaoxing.activity.dto.event.ActivityReleaseEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.activity.ActivityReleaseScopeChangeQueue;
import com.chaoxing.activity.service.queue.event.ActivityCancelReleaseEventQueue;
import com.chaoxing.activity.service.queue.event.ActivityReleaseEventQueue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

/**活动发布状态改变事件服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseStatusChangeEventService
 * @description
 * @blame wwb
 * @date 2021-10-27 17:35:06
 */
@Slf4j
@Service
public class ActivityReleaseStatusChangeEventService {

    @Resource
    private ActivityReleaseScopeChangeQueue activityReleaseScopeChangeQueueService;
    @Resource
    private ActivityReleaseEventQueue activityReleaseEventQueue;
    @Resource
    private ActivityCancelReleaseEventQueue activityCancelReleaseEventQueue;

    /**发布状态的改变
     * @Description
     * @author wwb
     * @Date 2021-03-26 20:04:29
     * @param activity
     * @return void
     */
    public void releaseStatusChange(Activity activity) {
        Integer activityId = activity.getId();
        Boolean released = Optional.ofNullable(activity.getReleased()).orElse(false);
        Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
        if (released) {
            ActivityReleaseEventOrigin activityReleaseEventOrigin = ActivityReleaseEventOrigin.builder()
                    .activityId(activityId)
                    .timestamp(timestamp)
                    .build();
            activityReleaseEventQueue.push(activityReleaseEventOrigin);
        } else {
            ActivityCancelReleaseEventOrigin activityCancelReleaseEventOrigin = ActivityCancelReleaseEventOrigin.builder()
                    .activityId(activityId)
                    .timestamp(timestamp)
                    .build();
            activityCancelReleaseEventQueue.push(activityCancelReleaseEventOrigin);
        }
        // 活动发布范围改变
        activityReleaseScopeChangeQueueService.push(activityId);
    }

}