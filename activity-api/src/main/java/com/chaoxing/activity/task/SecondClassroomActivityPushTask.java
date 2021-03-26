package com.chaoxing.activity.task;

import com.chaoxing.activity.service.form.ActivityFormRecordService;
import com.chaoxing.activity.service.queue.SecondClassroomActivityPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**第二课堂活动推送任务
 * @author wwb
 * @version ver 1.0
 * @className SecondClassroomActivityPushTask
 * @description
 * @blame wwb
 * @date 2021-03-26 17:03:11
 */
@Slf4j
@Component
public class SecondClassroomActivityPushTask {

	@Resource
	private SecondClassroomActivityPushQueueService secondClassroomActivityPushQueueService;
	@Resource
	private ActivityFormRecordService activityFormRecordService;

	/**推送
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:04:23
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void push() {
		Integer activityId = secondClassroomActivityPushQueueService.get();
		if (activityId == null) {
			return;
		}
		try {
			activityFormRecordService.handleActivityPush(activityId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("第二课堂活动:{} 推送error:{}", activityId, e.getMessage());
			secondClassroomActivityPushQueueService.add(activityId);
		}

	}

}