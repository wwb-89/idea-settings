package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.ActivityIsAboutEndHandleService;
import com.chaoxing.activity.service.activity.ActivityStatusUpdateService;
import com.chaoxing.activity.service.queue.activity.ActivityStatusUpdateQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动状态更新任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusSyncTask
 * @description
 * @blame wwb
 * @date 2020-12-05 15:17:38
 */
@Slf4j
@Component
public class ActivityStatusUpdateTask {

	@Resource
	private ActivityStatusUpdateQueueService activityStatusUpdateQueueService;
	@Resource
	private ActivityStatusUpdateService activityStatusUpdateService;
	@Resource
	private ActivityIsAboutEndHandleService activityEndNoticeService;

	/**更新活动状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-11 14:34:40
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 100L)
	public void syncStartTimeStatus() {
		ZSetOperations.TypedTuple<Integer> startTimeQueueData = activityStatusUpdateQueueService.getStartTimeQueueData();
		if (startTimeQueueData == null) {
			return;
		}
		Double startTime = startTimeQueueData.getScore();
		// 判断时间是不是小于等于当前时间
		long l = startTime.longValue();
		Integer activityId = startTimeQueueData.getValue();
		if (activityStatusUpdateService.statusUpdate(activityId, l)) {
			activityStatusUpdateQueueService.removeStartTime(activityId);
		}
	}

	/**更新活动状态
	 * @Description
	 * @author wwb
	 * @Date 2020-12-11 14:34:40
	 * @param
	 * @return void
	 */
	@Scheduled(fixedDelay = 100L)
	public void syncEndTimeStatus() {
		ZSetOperations.TypedTuple<Integer> endTimeQueueData = activityStatusUpdateQueueService.getEndTimeQueueData();
		if (endTimeQueueData == null) {
			return;
		}
		Double endTime = endTimeQueueData.getScore();
		// 判断时间是不是小于等于当前时间
		long l = endTime.longValue();
		Integer activityId = endTimeQueueData.getValue();
		if (activityStatusUpdateService.statusUpdate(activityId, l)) {
			// 活动结束发通知
			activityEndNoticeService.sendActivityIsAboutEndNotice(activityId);
			activityStatusUpdateQueueService.removeEndTime(activityId);
		}
	}

}
