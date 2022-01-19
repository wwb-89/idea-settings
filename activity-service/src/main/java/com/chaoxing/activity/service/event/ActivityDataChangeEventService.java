package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityChangeDTO;
import com.chaoxing.activity.dto.event.activity.*;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.activity.ActivityTimingReleaseQueue;
import com.chaoxing.activity.service.queue.event.activity.*;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
	@Resource
	private ActivityPeriodChangeEventQueue activityPeriodChangeEventQueue;
	@Resource
	private ActivityCreditChangeEventQueue activityCreditChangeEventQueue;

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
		ActivityChangeDTO activityChange = ActivityChangeDTO.build(oldActivity, activity);
		// 活动标题修改
		activityNameChangeHandle(activityChange);
		// 活动积分改变处理
		activityIntegralChangeHandle(activityChange);
		// 活动学时改变处理
		activityPeriodChangeHandle(activityChange);
		// 活动学分改变处理
		activityCreditChangeHandle(activityChange);
		// 活动时间改变处理
		activityTimeChangeHandle(activityChange);
		// 活动定时发布处理
		activityTimingReleaseHandle(activityChange, loginUser);
		// 活动地点时间改变处理
		activityAddressTimeChangeHandle(activityChange);
		// 活动名称、时间改变处理
		activityNameTimeChangeHandle(activityChange);
		// 门户模版改变
		activityWebTemplateIdChangeHandle(activityChange);
		// 活动改变处理
		ActivityChangeEventOrigin activityChangeEventOrigin = ActivityChangeEventOrigin.builder()
				.activityId(activity.getId())
				.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
				.build();
		activityChangeEventQueue.push(activityChangeEventOrigin);
		// 活动封面改变处理
		activityCoverChangeHandle(activityChange);
	}

	/**活动封面改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 18:30:59
	 * @param activityChange
	 * @return void
	*/
	private void activityCoverChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getCoverChanged()) {
			ActivityCoverChangeEventOrigin eventOrigin = ActivityCoverChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityCoverChangeEventQueue.push(eventOrigin);
		}

	}
	/**活动名称改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 17:47:31
	 * @param activityChange
	 * @return void
	*/
	private void activityNameChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getNameChanged()) {
			ActivityNameChangedEventOrigin eventOrigin = ActivityNameChangedEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityNameChangeEventQueue.push(eventOrigin);
		}

	}
	/**活动积分改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 17:29:58
	 * @param activityChange
	 * @return void
	*/
	private void activityIntegralChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getAdd()) {
			return;
		}
		if (activityChange.getIntegralChanged()) {
			ActivityIntegralChangeEventOrigin eventOrigin = ActivityIntegralChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityIntegralChangeEventQueue.push(eventOrigin);
		}
	}
	/**活动学时的改变
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-14 10:14:26
	 * @param activityChange
	 * @return void
	*/
	private void activityPeriodChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getAdd()) {
			return;
		}
		if (activityChange.getPeriodChanged()) {
			ActivityPeriodChangeEventOrigin eventOrigin = ActivityPeriodChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityPeriodChangeEventQueue.push(eventOrigin);
		}
	}
	/**活动学分的改变
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-14 10:14:42
	 * @param activityChange
	 * @return void
	*/
	private void activityCreditChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getAdd()) {
			return;
		}
		if (activityChange.getCreditChanged()) {
			ActivityCreditChangeEventOrigin eventOrigin = ActivityCreditChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityCreditChangeEventQueue.push(eventOrigin);
		}
	}
	/**活动定时发布处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 17:45:57
	 * @param activityChange
	 * @param loginUser
	 * @return void
	*/
	private void activityTimingReleaseHandle(ActivityChangeDTO activityChange, LoginUserDTO loginUser) {
		Activity activity = activityChange.getNewActivity();
		Activity oldActivity = activityChange.getOldActivity();

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
		}
	}
	/**活动地点时间改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-27 15:51:11
	 * @param activityChange
	 * @return void
	*/
	private void activityAddressTimeChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getAdd()) {
			return;
		}
		if (activityChange.getAddressChanged() || activityChange.getTimeChanged()) {
			ActivityAddressTimeChangeEventOrigin eventOrigin = ActivityAddressTimeChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityAddressTimeChangeEventQueue.push(eventOrigin);
		}
	}

	/**活动名称、时间改变处理
	 * @Description 目前更新作品征集信息
	 * @author wwb
	 * @Date 2021-10-27 16:01:50
	 * @param activityChange
	 * @return void
	*/
	private void activityNameTimeChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getNameChanged() || activityChange.getTimeChanged()) {
			ActivityNameTimeChangeEventOrigin eventOrigin = ActivityNameTimeChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityNameTimeChangeEventQueue.push(eventOrigin);
		}
	}

	/**处理门户模版的改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-08 20:55:34
	 * @param activityChange
	 * @return void
	*/
	private void activityWebTemplateIdChangeHandle(ActivityChangeDTO activityChange) {
		if (activityChange.getWebTemplateChanged()) {
			ActivityWebTemplateChangeEventOrigin eventOrigin = ActivityWebTemplateChangeEventOrigin.builder()
					.activityId(activityChange.getNewActivity().getId())
					.timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
					.build();
			activityWebTemplateChangeEventQueue.push(eventOrigin);
		}
	}

	/**活动时间改变处理
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-26 18:50:34
	 * @param activityChange
	 * @return void
	*/
	private void activityTimeChangeHandle(ActivityChangeDTO activityChange) {
		LocalDateTime now = LocalDateTime.now();
		Long timestamp = DateUtils.date2Timestamp(now);
		Activity activity = activityChange.getNewActivity();
		Integer activityId = activity.getId();
		if (activityChange.getStartTimeChanged()) {
			ActivityStartTimeReachEventOrigin activityStartTimeReachEventOrigin = ActivityStartTimeReachEventOrigin.builder()
					.activityId(activityId)
					.startTime(activity.getStartTime())
					.timestamp(timestamp)
					.build();
			activityStartTimeReachEventQueue.push(activityStartTimeReachEventOrigin);
			if (activity.getStartTime().isAfter(now)) {
				// 活动即将开始
				ActivityAboutStartEventOrigin activityAboutStartEventOrigin = ActivityAboutStartEventOrigin.builder()
						.activityId(activityId)
						.startTime(activity.getStartTime())
						.timestamp(timestamp)
						.build();
				activityAboutStartEventQueue.push(activityAboutStartEventOrigin);
			}
		}
		if (activityChange.getEndTimeChanged()) {
			ActivityEndTimeReachEventOrigin activityEndTimeReachEventOrigin = ActivityEndTimeReachEventOrigin.builder()
					.activityId(activityId)
					.endTime(activity.getEndTime())
					.timestamp(timestamp)
					.build();
			activityEndTimeReachEventQueue.push(activityEndTimeReachEventOrigin);
			if (activity.getEndTime().isAfter(now)) {
				// 活动即将结束
				ActivityAboutEndEventOrigin activityAboutEndEventOrigin = ActivityAboutEndEventOrigin.builder()
						.activityId(activityId)
						.endTime(activity.getEndTime())
						.timestamp(timestamp)
						.build();
				activityAboutEndEventQueue.push(activityAboutEndEventOrigin);
			}
		}

	}

}