package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityHandleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动状态同步任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusSyncTask
 * @description
 * @blame wwb
 * @date 2020-12-05 15:17:38
 */
@Component
public class ActivityStatusSyncTask {

	@Resource
	private ActivityHandleService activityHandleService;

	@Scheduled(cron = "1 0 0 * * ?")
	public void syncStatus() {
		activityHandleService.syncStatus();
	}

}
