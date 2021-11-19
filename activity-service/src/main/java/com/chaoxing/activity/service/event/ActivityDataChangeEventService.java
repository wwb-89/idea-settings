package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.event.activity.*;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.activity.ActivityTimingReleaseQueue;
import com.chaoxing.activity.service.queue.event.activity.*;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**活动数据改变事件服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataChangeEventService
 * @description
 * @blame wwb
 * @date 2021-03-26 14:53:00
 */
@Slf4j
@Service
public class ActivityDataChangeEventService {

	@Resource
	private ActivityTimingReleaseQueue activityTimingReleaseQueueService;
	@Resource
	private ActivityIntegralChangeEventQueue activityIntegralChangeEventQueue;
	@Resource
	private ActivityCoverChangeEventQueue activityCoverChangeEventQueue;
	@Resource
	private ActivityNameChangeEventQueue activityNameChangeEventQueue;
	@Resource
	private ActivityAboutStartEventQueue activityAboutStartEventQueue;
	@Resource
	private ActivityAboutEndEventQueue activityAboutEndEventQueue;
	@Resource
	private ActivityStartTimeReachEventQueue activityStartTimeReachEventQueue;
	@Resource
	private ActivityEndTimeReachEventQueue activityEndTimeReachEventQueue;
	@Resource
	private ActivityAddressTimeChangeEventQueue activityAddressTimeChangeEventQueue;
	@Resource
	private ActivityNameTimeChangeEventQueue activityNameTimeChangeEventQueue;
	@Resource
	private ActivityChangeEventQueue activityChangeEventQueue;
	@Resource
	private ActivityWebTemplateChangeEventQueue activityWebTemplateChangeEventQueue;

	/**活动数据改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:54:18
	 * @param activity
	 * @param oldActivity 原活动
	 * @param loginUser
	 * @return void
	*/
	public void dataChange(Activity activity, Activity oldActivity, LoginUserDTO loginUser) {
		// 活动标题修改
		activityNameChangeHandle(activity, oldActivity);
		// 活动积分改变处理
		activityIntegralChangeHandle(activity, oldActivity);
		// 活动时间改变处理
		activityTimeChangeHandle(activity, oldActivity);
		// 活动定时发布处理
		activityTimingReleaseHandle(activity, oldActivity, loginUser);
		// 活动地点时间改变处理
		activityAddressTimeChangeHandle(activity, oldActivity);
		// 活动名称、时间改变处理
		activityNameTimeChangeHandle(activity, oldActivity);
		// 门户模版改变
		activityWebTemplateIdChangeHandle(activity, oldActivity);
		// 活动改变处理
		ActivityChangeEventOrigin activityChangeEventOrigin = ActivityChangeEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityChangeEventQueue.push(activityChangeEventOrigin);
		// 活动封面改变处理
		activityCoverChangeHandle(activity, oldActivity);
	}

	/**活动封面改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 18:30:59
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityCoverChangeHandle(Activity activity, Activity oldActivity) {
		if (oldActivity != null && Objects.equals(activity.getCoverCloudId(), oldActivity.getCoverCloudId())) {
			return;
		}
		ActivityCoverChangeEventOrigin eventOrigin = ActivityCoverChangeEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityCoverChangeEventQueue.push(eventOrigin);
	}
	/**活动名称改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 17:47:31
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityNameChangeHandle(Activity activity, Activity oldActivity) {
		if (oldActivity != null && Objects.equals(activity.getName(), oldActivity.getName())) {
			// 活动名称没改变
			return;
		}
		ActivityNameChangedEventOrigin eventOrigin = ActivityNameChangedEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityNameChangeEventQueue.push(eventOrigin);
	}
	/**活动积分改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 17:29:58
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityIntegralChangeHandle(Activity activity, Activity oldActivity) {
		if (activity == null || oldActivity == null) {
			return;
		}
		BigDecimal integral = Optional.ofNullable(activity.getIntegral()).orElse(BigDecimal.ZERO);
		BigDecimal oldIntegral = Optional.ofNullable(oldActivity.getIntegral()).orElse(BigDecimal.ZERO);
		if (integral.compareTo(oldIntegral) != 0) {
			// 积分改变了
			ActivityIntegralChangeEventOrigin eventOrigin = ActivityIntegralChangeEventOrigin.builder()
					.activityId(activity.getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityIntegralChangeEventQueue.push(eventOrigin);
		}
	}
	/**活动定时发布处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 17:45:57
	 * @param activity
	 * @param oldActivity
	 * @param loginUser
	 * @return void
	*/
	private void activityTimingReleaseHandle(Activity activity, Activity oldActivity, LoginUserDTO loginUser) {
		Integer activityId = activity.getId();
		// 活动定时发布
		Boolean released = Optional.ofNullable(activity.getReleased()).orElse(false);
		if (released) {
			// 已发布则不做处理
			return;
		}
		Boolean timingRelease = Optional.ofNullable(activity.getTimingRelease()).orElse(false);
		if (timingRelease) {
			LocalDateTime timingReleaseTime = activity.getTimingReleaseTime();
			ActivityTimingReleaseQueue.QueueParamDTO queueParam = ActivityTimingReleaseQueue.QueueParamDTO.builder()
					.activityId(activity.getId())
					.releaseTime(timingReleaseTime)
					.loginUser(loginUser)
					.build();
			Boolean timingReleaseTimeChange = true;
			if (oldActivity != null) {
				LocalDateTime oldTimingReleaseTime = oldActivity.getTimingReleaseTime();
				if (oldTimingReleaseTime != null) {
					timingReleaseTimeChange = timingReleaseTime.compareTo(oldTimingReleaseTime) != 0;
				}
			}
			if (timingReleaseTimeChange) {
				// 定时发布
				activityTimingReleaseQueueService.push(queueParam);
			}
		} else {
			// 取消定时发布
			activityTimingReleaseQueueService.remove(activityId);
		}
	}
	/**活动地点时间改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-27 15:51:11
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityAddressTimeChangeHandle(Activity activity, Activity oldActivity) {
		if (oldActivity == null) {
			return;
		}
		boolean changed;
		changed = !Objects.equals(activity.getAddress(), oldActivity.getAddress()) || !Objects.equals(activity.getDetailAddress(), oldActivity.getDetailAddress());
		if (!changed) {
			changed = activity.getStartTime().compareTo(oldActivity.getStartTime()) != 0 || activity.getEndTime().compareTo(oldActivity.getEndTime()) != 0;
		}
		boolean activityEnded = activity.getEndTime().isBefore(LocalDateTime.now());
		if (!changed || activityEnded) {
			return;
		}
		ActivityAddressTimeChangeEventOrigin eventOrigin = ActivityAddressTimeChangeEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityAddressTimeChangeEventQueue.push(eventOrigin);
	}

	/**活动名称、时间改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-27 16:01:50
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityNameTimeChangeHandle(Activity activity, Activity oldActivity) {
		if (oldActivity != null) {
			boolean changed;
			changed = !Objects.equals(activity.getName(), oldActivity.getName());
			if (!changed) {
				changed = activity.getStartTime().compareTo(oldActivity.getStartTime()) != 0 || activity.getEndTime().compareTo(oldActivity.getEndTime()) != 0;
			}
			if (!changed) {
				return;
			}
		}
		ActivityNameTimeChangeEventOrigin eventOrigin = ActivityNameTimeChangeEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityNameTimeChangeEventQueue.push(eventOrigin);
	}

	/**处理门户模版的改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-08 20:55:34
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityWebTemplateIdChangeHandle(Activity activity, Activity oldActivity) {
		if (oldActivity != null && Objects.equals(activity.getPageId(), oldActivity.getPageId()) && activity.getWebsiteId() != null) {
			return;
		}
		ActivityWebTemplateChangeEventOrigin eventOrigin = ActivityWebTemplateChangeEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityWebTemplateChangeEventQueue.push(eventOrigin);
	}

	/**活动时间改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 18:50:34
	 * @param activity
	 * @param oldActivity
	 * @return void
	*/
	private void activityTimeChangeHandle(Activity activity, Activity oldActivity) {
		if (oldActivity == null || activity.getStartTime().compareTo(oldActivity.getStartTime()) != 0 || activity.getEndTime().compareTo(oldActivity.getEndTime()) != 0) {
			// 新增活动或者时间发生了改变
			Integer activityId = activity.getId();
			Integer marketId = activity.getMarketId();
			Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
			if (oldActivity == null || activity.getStartTime().compareTo(oldActivity.getStartTime()) != 0) {
				ActivityAboutStartEventOrigin activityAboutStartEventOrigin = ActivityAboutStartEventOrigin.builder()
						.activityId(activityId)
						.startTime(activity.getStartTime())
						.timestamp(timestamp)
						.build();
				activityAboutStartEventQueue.push(activityAboutStartEventOrigin);
			}
			if (oldActivity == null || activity.getEndTime().compareTo(oldActivity.getEndTime()) != 0) {
				ActivityAboutEndEventOrigin activityAboutEndEventOrigin = ActivityAboutEndEventOrigin.builder()
						.activityId(activityId)
						.endTime(activity.getEndTime())
						.timestamp(timestamp)
						.build();
				activityAboutEndEventQueue.push(activityAboutEndEventOrigin);
			}
			ActivityStartTimeReachEventOrigin activityStartTimeReachEventOrigin = ActivityStartTimeReachEventOrigin.builder()
					.activityId(activityId)
					.startTime(activity.getStartTime())
					.timestamp(timestamp)
					.build();
			activityStartTimeReachEventQueue.push(activityStartTimeReachEventOrigin);
			ActivityEndTimeReachEventOrigin activityEndTimeReachEventOrigin = ActivityEndTimeReachEventOrigin.builder()
					.activityId(activityId)
					.endTime(activity.getEndTime())
					.timestamp(timestamp)
					.build();
			activityEndTimeReachEventQueue.push(activityEndTimeReachEventOrigin);
		}
	}

}