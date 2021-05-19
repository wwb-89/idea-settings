package com.chaoxing.activity.task;

import com.chaoxing.activity.service.form.ActivityFormRecordService;
import com.chaoxing.activity.service.queue.SecondClassroomActivityPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动表单推送任务
 * @author wwb
 * @version ver 1.0
 * @className SecondClassroomActivityPushTask
 * @description
 * @blame wwb
 * @date 2021-03-26 17:03:11
 */
@Slf4j
@Component
public class ActivityFormPushTask {

	@Resource
	private SecondClassroomActivityPushQueueService secondClassroomActivityPushQueueService;
	@Resource
	private ActivityFormRecordService activityFormRecordService;

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:04:23
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void add() {
		Integer activityId = secondClassroomActivityPushQueueService.getAdd();
		if (activityId == null) {
			return;
		}
		try {
			activityFormRecordService.add(activityId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("第二课堂活动:{} 推送error:{}", activityId, e.getMessage());
			secondClassroomActivityPushQueueService.add(activityId);
		}
	}

	/**更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:32:25
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void update() {
		Integer activityId = secondClassroomActivityPushQueueService.getUpdate();
		if (activityId == null) {
			return;
		}
		try {
			activityFormRecordService.update(activityId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("第二课堂活动:{} 推送error:{}", activityId, e.getMessage());
			secondClassroomActivityPushQueueService.add(activityId);
		}
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:39:24
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void delete() {
		Integer activityId = secondClassroomActivityPushQueueService.getDelete();
		if (activityId == null) {
			return;
		}
		try {
			activityFormRecordService.delete(activityId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("第二课堂活动:{} 推送error:{}", activityId, e.getMessage());
			secondClassroomActivityPushQueueService.delete(activityId);
		}
	}

}