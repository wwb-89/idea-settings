package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityCoverService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动封面填充任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverUrlFillTask
 * @description 活动新增或修改封面后将云盘资源id存入队列中， 定时去拉取队列来更新活动的封面
 * @blame wwb
 * @date 2021-01-20 10:35:55
 */
@Component
public class ActivityCoverUrlFillTask {

	@Resource
	private ActivityCoverService activityCoverService;

	@Scheduled(fixedDelay = 60 * 1000)
	public void syncActivityCoverUrl() {
		activityCoverService.syncActivityCoverUrl();
	}

}