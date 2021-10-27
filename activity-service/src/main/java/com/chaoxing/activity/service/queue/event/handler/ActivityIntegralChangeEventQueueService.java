package com.chaoxing.activity.service.queue.event.handler;

import com.chaoxing.activity.dto.event.ActivityIntegralChangeEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityIntegralChangeQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**活动积分改变事件队列数据消费服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeEventQueueService
 * @description
 * 1、机构用户统计中用户获得的积分更新
 * 2、通知更新表单中的用户行为数据
 * @blame wwb
 * @date 2021-10-26 16:39:30
 */
@Slf4j
@Service
public class ActivityIntegralChangeEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityIntegralChangeQueue activityIntegralChangeQueue;
    @Resource
    private UserStatSummaryHandleService userStatSummaryHandleService;

    @Transactional(rollbackFor = Exception.class)
    public void handle(ActivityIntegralChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        // 机构用户统计中用户获得的积分更新
        userStatSummaryHandleService.updateActivityUserIntegral(activity.getId(), activity.getIntegral());
        Integer signId = activity.getSignId();
        if (signId != null) {
            activityIntegralChangeQueue.push(signId);
        }
    }

}