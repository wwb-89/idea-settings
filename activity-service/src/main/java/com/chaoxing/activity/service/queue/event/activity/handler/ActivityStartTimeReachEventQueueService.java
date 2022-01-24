package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityStartTimeReachEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityStatusService;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动开始时间到达事件任务服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartTimeReachEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 11:22:56
 */
@Slf4j
@Service
public class ActivityStartTimeReachEventQueueService {

    @Resource
    private ActivityStatusService activityStatusService;
    @Resource
    private ActivityQueryService activityQueryService;

    public void handle(ActivityStartTimeReachEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        if (eventOrigin.getStartTime().compareTo(activity.getStartTime()) != 0) {
            log.info("忽略活动开始时间到达事件任务, 记录的活动开始时间:{}, 当前活动开始时间:{}", DateUtils.date2Timestamp(eventOrigin.getStartTime()), DateUtils.date2Timestamp(activity.getStartTime()));
            return;
        }
        activityStatusService.statusUpdate(activityId);
    }

}