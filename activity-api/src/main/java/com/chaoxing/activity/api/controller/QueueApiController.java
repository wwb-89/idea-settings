package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.event.activity.*;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.queue.activity.ActivityStatQueue;
import com.chaoxing.activity.service.queue.event.activity.*;
import com.chaoxing.activity.service.queue.user.OrgUserDataPushQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.util.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
	private ActivityStatQueue activityStatQueueService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;
	@Resource
	private ActivityWebTemplateChangeEventQueue activityWebTemplateChangeEventQueue;
	@Resource
	private ActivityCoverChangeEventQueue activityCoverChangeEventQueue;
	@Resource
	private ActivityAboutStartEventQueue activityAboutStartEventQueue;
	@Resource
	private ActivityAboutEndEventQueue activityAboutEndEventQueue;
	@Resource
	private ActivityStartTimeReachEventQueue activityStartTimeReachEventQueue;
	@Resource
	private ActivityEndTimeReachEventQueue activityEndTimeReachEventQueue;
	@Resource
	private OrgUserDataPushQueue orgUserDataPushQueue;
	@Resource
	private UserStatSummaryQueryService userStatSummaryQueryService;

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

	@RequestMapping("rerun-failed/activity/stat")
	public RestRespDTO rerunFailedStatActivity() {
		activityStatQueueService.rerunFailedStatActivityTask();
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
				ActivityCoverChangeEventOrigin eventOrigin = ActivityCoverChangeEventOrigin.builder()
						.activityId(activity.getId())
						.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
						.build();
				activityCoverChangeEventQueue.push(eventOrigin);
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
			Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
			for (Integer activityId : activityIds) {
				ActivityWebTemplateChangeEventOrigin eventOrigin = ActivityWebTemplateChangeEventOrigin.builder()
						.activityId(activityId)
						.timestamp(timestamp)
						.build();
				activityWebTemplateChangeEventQueue.push(eventOrigin);
			}
		}
		return RestRespDTO.success();
	}

	/**初始化活动即将开始任务队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-03 16:56:30
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity_about_start")
	public RestRespDTO initActivityAboutStart() {
		List<Activity> activities = activityQueryService.listNotStart();
		if (CollectionUtils.isNotEmpty(activities)) {
			Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
			for (Activity activity : activities) {
				ActivityAboutStartEventOrigin eventOrigin = ActivityAboutStartEventOrigin.builder()
						.activityId(activity.getId())
						.startTime(activity.getStartTime())
						.timestamp(timestamp)
						.build();
				activityAboutStartEventQueue.push(eventOrigin);
			}
		}
		return RestRespDTO.success();
	}
	/**初始化活动即将结束任务队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-03 16:56:43
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity_about_end")
	public RestRespDTO initActivityAboutEnd() {
		List<Activity> activities = activityQueryService.listNotEnd();
		if (CollectionUtils.isNotEmpty(activities)) {
			Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
			for (Activity activity : activities) {
				ActivityAboutEndEventOrigin eventOrigin = ActivityAboutEndEventOrigin.builder()
						.activityId(activity.getId())
						.endTime(activity.getEndTime())
						.timestamp(timestamp)
						.build();
				activityAboutEndEventQueue.push(eventOrigin);
			}
		}
		return RestRespDTO.success();
	}
	/**初始化活动开始时间将要到达任务队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-03 16:56:59
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity_start_time_reach")
	public RestRespDTO initActivityStartTimeReach() {
		List<Activity> activities = activityQueryService.listNotStart();
		if (CollectionUtils.isNotEmpty(activities)) {
			Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
			for (Activity activity : activities) {
				ActivityStartTimeReachEventOrigin eventOrigin = ActivityStartTimeReachEventOrigin.builder()
						.activityId(activity.getId())
						.startTime(activity.getStartTime())
						.timestamp(timestamp)
						.build();
				activityStartTimeReachEventQueue.push(eventOrigin);
			}
		}
		return RestRespDTO.success();
	}
	/**初始化活动结束时间将要到达任务队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-03 17:18:20
	 * @param 
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("init/activity_end_time_reach")
	public RestRespDTO initActivityEndTimeReach() {
		List<Activity> activities = activityQueryService.listNotEnd();
		if (CollectionUtils.isNotEmpty(activities)) {
			Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
			for (Activity activity : activities) {
				ActivityEndTimeReachEventOrigin eventOrigin = ActivityEndTimeReachEventOrigin.builder()
						.activityId(activity.getId())
						.endTime(activity.getEndTime())
						.timestamp(timestamp)
						.build();
				activityEndTimeReachEventQueue.push(eventOrigin);
			}
		}
		return RestRespDTO.success();
	}

	/**机构下所有活动的用户数据推送
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-17 17:08:35
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("org/{fid}/activity-user-data")
	public RestRespDTO pushOrgActivityUserData(@PathVariable Integer fid) {
		List<Activity> activities = activityQueryService.listByFid(fid);
		List<Integer> activityIds = activities.stream().filter(v -> !Objects.equals(Activity.StatusEnum.DELETED.getValue(), v.getStatus())).map(Activity::getId).collect(Collectors.toList());
		// 查询用户活动汇总数据
		for (Integer activityId : activityIds) {
			List<UserStatSummary> userStatSummaries = userStatSummaryQueryService.listActivityStatData(activityId);
			for (UserStatSummary userStatSummary : userStatSummaries) {
				orgUserDataPushQueue.push(new OrgUserDataPushQueue.QueueParamDTO(userStatSummary.getUid(), userStatSummary.getActivityId()));
			}
		}

		return RestRespDTO.success();
	}

}