package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityStatusHandleService;
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
	private ActivityStatusHandleService activityStatusHandleService;

	/**每秒执行一次
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-11 14:34:40
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1000)
	public void syncStartTimeStatus() {
		activityStatusHandleService.startStatusSync();
	}

	/**每秒执行一次
	 * @Description
	 * @author wwb
	 * @Date 2020-12-11 14:34:40
	 * @param
	 * @return void
	 */
	@Scheduled(fixedDelay = 1000)
	public void syncEndTimeStatus() {
		activityStatusHandleService.endStatusSync();
	}

}
