package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityPeriodChangeEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动学时改变事件队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityPeriodChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2022-01-13 17:14:56
 */
@Slf4j
@Service
public class ActivityPeriodChangeEventQueueService  {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private UserStatSummaryHandleService userStatSummaryHandleService;

	/**处理
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-13 17:17:40
	 * @param eventOrigin
	 * @return void
	*/
	public void handle(ActivityPeriodChangeEventOrigin eventOrigin) {
		if (eventOrigin == null) {
			return;
		}

		Integer activityId = eventOrigin.getActivityId();
		Activity activity = activityQueryService.getById(activityId);
		if (activity == null) {
			return;
		}
		// 用户活动汇总数据的活动积分和活的的积分变更
		userStatSummaryHandleService.updateActivityPeriod(activity.getId(), activity.getPeriod());
	}

}