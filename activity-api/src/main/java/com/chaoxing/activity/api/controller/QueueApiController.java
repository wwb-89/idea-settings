package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.queue.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**队列api服务
 * @author wwb
 * @version ver 1.0
 * @className QueueApiController
 * @description
 * @blame wwb
 * @date 2021-03-26 18:30:34
 */
@RestController
@RequestMapping("queue")
public class QueueApiController {

	@Resource
	private ActivityStatQueueService activityStatQueueService;
	@Resource
	private ActivityIsAboutToStartQueueService activityIsAboutToStartQueueService;
	@Resource
	private ActivityStatusUpdateQueueService activityStatusUpdateQueueService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;
	@Resource
	private ActivityCoverUrlSyncQueueService activityCoverUrlSyncQueueService;
	@Resource
	private ActivityWebsiteIdSyncQueueService activityWebsiteIdSyncQueueService;

	/**初始化签到的开始结束时间队列
	 * @Description
	 * @author wwb
	 * @Date 2021-03-25 20:16:34
	 * @param
	 * @return com.chaoxing.sign.dto.RestRespDTO
	 */
	@RequestMapping("init/activity-is-about-to-start")
	public RestRespDTO initActivityIsAboutToStartQueue() {
		List<Activity> activities = activityQueryService.list();
		if (CollectionUtils.isNotEmpty(activities)) {
			for (Activity activity : activities) {
				activityIsAboutToStartQueueService.add(activity);
			}
		}
		return RestRespDTO.success();
	}

	/**初始化活动的状态队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-21 15:31:45
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity-status")
	public RestRespDTO initActivityStatusQueue() {
		List<Activity> activities = activityQueryService.list();
		if (CollectionUtils.isNotEmpty(activities)) {
			for (Activity activity : activities) {
				activityStatusUpdateQueueService.addTime(activity);
			}
		}
		return RestRespDTO.success();
	}
	/**初始化活动的汇总统计队列
	 * @Description
	 * @author wwb
	 * @Date 2021-04-21 15:31:45
	 * @param
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity-stat")
	public RestRespDTO initActivityStatQueue() {
		activityStatSummaryHandlerService.addOrUpdateAllActivityStatSummary();
		return RestRespDTO.success();
	}

	/**初始化活动的统计队列
	 * @Description
	 * @author wwb
	 * @Date 2021-04-21 15:31:45
	 * @param
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity/stat")
	public RestRespDTO initStatActivityQueue() {
		activityStatQueueService.batchAddActivityStatTask();
		return RestRespDTO.success();
	}

	/**初始化活动封面队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-31 14:31:54
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity-cover")
	public RestRespDTO initActivityCoverQueue() {
		List<Activity> activities = activityQueryService.listEmptyCoverUrl();
		if (CollectionUtils.isNotEmpty(activities)) {
			for (Activity activity : activities) {
				activityCoverUrlSyncQueueService.push(activity.getId());
			}
		}
		return RestRespDTO.success();
	}

	/**活动网站id同步
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 10:21:30
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity-website-id-sync")
	public RestRespDTO activityWebsiteIdSync() {
		List<Integer> activityIds = activityQueryService.listEmptyWebsiteIdActivityId();
		if (CollectionUtils.isNotEmpty(activityIds)) {
			for (Integer activityId : activityIds) {
				activityWebsiteIdSyncQueueService.add(activityId);
			}
		}
		return RestRespDTO.success();
	}



}