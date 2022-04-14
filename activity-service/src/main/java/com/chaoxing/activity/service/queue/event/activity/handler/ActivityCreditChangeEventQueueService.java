package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.dto.event.activity.ActivityCreditChangeEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动学分改变事件队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreditChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2022-01-13 17:24:57
 */
@Slf4j
@Service
public class ActivityCreditChangeEventQueueService {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private UserStatSummaryHandleService userStatSummaryHandleService;

	/**处理
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-13 17:25:36
	 * @param eventOrigin
	 * @return void
	*/
	public void handle(ActivityCreditChangeEventOrigin eventOrigin) {
		if (eventOrigin == null) {
			return;
		}

		Integer activityId = eventOrigin.getActivityId();
		Activity activity = activityQueryService.getById(activityId);
		if (activity == null) {
			return;
		}
		// 用户活动汇总数据的活动积分和活的的积分变更
		userStatSummaryHandleService.updateActivityCredit(activity.getId(), activity.getCredit());
	}

}