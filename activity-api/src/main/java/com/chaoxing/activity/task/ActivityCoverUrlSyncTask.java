package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import com.chaoxing.activity.service.queue.ActivityCoverUrlSyncQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动封面url同步任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverUrlSyncTask
 * @description 活动新增或修改封面后将云盘资源id存入队列中， 定时去拉取队列来更新活动的封面
 * @blame wwb
 * @date 2021-01-20 10:35:55
 */
@Component
public class ActivityCoverUrlSyncTask {

	@Resource
	private ActivityCoverUrlSyncQueueService activityCoverUrlSyncQueueService;
	@Resource
	private ActivityCoverUrlSyncService activityCoverService;

	/**同步活动封面url
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:19:44
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void syncActivityCoverUrl() throws InterruptedException {
		Integer activityId = activityCoverUrlSyncQueueService.pop();
		if (activityId == null) {
			return;
		}
		activityCoverService.syncActivityCoverUrl(activityId);
	}

}