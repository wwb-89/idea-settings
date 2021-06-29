package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.ActivityStatusUpdateService;
import com.chaoxing.activity.service.queue.activity.ActivityStatusUpdateQueueService;
import lombok.extern.slf4j.Slf4j;
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

	/**更新活动状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-11 14:34:40
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void syncStartTimeStatus() throws InterruptedException {
		ActivityStatusUpdateQueueService.QueueParamDTO queueParam = activityStatusUpdateQueueService.popStartTime();
		if (queueParam == null) {
			return;
		}
		try {
			activityStatusUpdateService.statusUpdate(queueParam.getActivityId());
		} catch (Exception e) {
			e.printStackTrace();
			activityStatusUpdateQueueService.pushStartTime(queueParam);
		}
	}

	/**更新活动状态
	 * @Description
	 * @author wwb
	 * @Date 2020-12-11 14:34:40
	 * @param
	 * @return void
	 */
	@Scheduled(fixedDelay = 1L)
	public void syncEndTimeStatus() throws InterruptedException {
		ActivityStatusUpdateQueueService.QueueParamDTO queueParam = activityStatusUpdateQueueService.popEndTime();
		if (queueParam == null) {
			return;
		}
		try {
			activityStatusUpdateService.statusUpdate(queueParam.getActivityId());
		} catch (Exception e) {
			e.printStackTrace();
			activityStatusUpdateQueueService.pushEndTime(queueParam);
		}
	}

}
