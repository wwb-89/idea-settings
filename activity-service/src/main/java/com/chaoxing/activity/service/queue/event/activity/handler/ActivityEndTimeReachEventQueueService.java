package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityEndTimeReachEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityStatusService;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动结束时间
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndTimeReachEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 11:25:04
 */
@Slf4j
@Service
public class ActivityEndTimeReachEventQueueService {

    @Resource
    private ActivityStatusService activityStatusService;
    @Resource
    private ActivityQueryService activityQueryService;

    public void handle(ActivityEndTimeReachEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        if (eventOrigin.getEndTime().compareTo(activity.getEndTime()) != 0) {
            log.info("忽略活动结束时间到达任务, 记录的活动结束时间:{}, 当前活动的结束时间:{}", DateUtils.date2Timestamp(eventOrigin.getEndTime()), DateUtils.date2Timestamp(activity.getEndTime()));
        }
        activityStatusService.statusUpdate(eventOrigin.getActivityId());
    }

}
