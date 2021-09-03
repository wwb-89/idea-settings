package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.UserSignedUpNoticeHandleService;
import com.chaoxing.activity.service.queue.activity.ActivityIsAboutToStartQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动即将开始任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIsAboutToStartTask
 * @description
 * @blame wwb
 * @date 2021-03-26 16:41:08
 */
@Slf4j
@Component
public class ActivityIsAboutToStartTask {

	@Resource
	private ActivityIsAboutToStartQueueService activityIsAboutToStartQueueService;
	@Resource
	private UserSignedUpNoticeHandleService activityIsAboutStartHandleService;

	/**活动即将开始
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:43:13
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void handle() throws InterruptedException {
		ActivityIsAboutToStartQueueService.QueueParamDTO queueParam = activityIsAboutToStartQueueService.popNoticeSignedUp();
		if (queueParam == null) {
			return;
		}
		try {
			activityIsAboutStartHandleService.sendActivityIsAboutStartNotice(queueParam.getActivityId());
		} catch (Exception e) {
			e.printStackTrace();
			activityIsAboutToStartQueueService.pushNoticeSignedUp(queueParam, true);
		}
	}

}