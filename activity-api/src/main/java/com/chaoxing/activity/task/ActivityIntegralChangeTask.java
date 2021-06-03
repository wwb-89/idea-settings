package com.chaoxing.activity.task;

import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.ActivityIntegralChangeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeTask
 * @description
 * @blame wwb
 * @date 2021-03-26 21:52:37
 */
@Slf4j
@Component
public class ActivityIntegralChangeTask {

	@Resource
	private ActivityIntegralChangeQueueService activityIntegralChangeQueueService;
	@Resource
	private SignApiService signApiService;

	/**通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 21:53:43
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void notice() throws InterruptedException {
		Integer signId = activityIntegralChangeQueueService.get();
		if (signId == null) {
			return;
		}
		try {
			signApiService.noticeSecondClassroomIntegralChange(signId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("通知报名签到:{} 第二课堂积分已变更error:{}", signId, e.getMessage());
			activityIntegralChangeQueueService.add(signId);
		}
	}

}