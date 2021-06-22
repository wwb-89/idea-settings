package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.activity.*;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**活动改变事件服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityChangeEventService
 * @description
 * @blame wwb
 * @date 2021-03-26 14:53:00
 */
@Slf4j
@Service
public class ActivityChangeEventService {

	@Resource
	private ActivityStatusUpdateQueueService activityStatusUpdateQueueService;
	@Resource
	private ActivityNameChangeNoticeQueueService activityNameChangeNoticeQueueService;
	@Resource
	private ActivityIsAboutToStartQueueService activityIsAboutToStartQueueService;
	@Resource
	private ActivityCoverUrlSyncQueueService activityCoverUrlSyncQueueService;
	@Resource
	private ActivityReleaseScopeChangeQueueService activityReleaseScopeChangeQueueService;
	@Resource
	private ActivityDataPushQueueService activityDataPushQueueService;
	@Resource
	private ActivityIntegralChangeQueueService activityIntegralChangeQueueService;
	@Resource
	private ActivityDataChangeQueueService activityDataChangeQueueService;
	@Resource
	private UserStatSummaryHandleService userStatSummaryService;
	@Resource
	private ActivityTimingReleaseQueueService activityTimingReleaseQueueService;

	/**活动数据改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:54:18
	 * @param activity
	 * @param oldActivity 原活动
	 * @param oldIntegralValue 原积分
	 * @param loginUser
	 * @return void
	*/
	public void dataChange(Activity activity, Activity oldActivity, BigDecimal oldIntegralValue, LoginUserDTO loginUser) {
		// 往表单推送数据
		activityDataPushQueueService.update(activity);
		// 订阅活动状态处理
		activityStatusUpdateQueueService.addTime(activity);
		// 通知门户修改网站的title
		Integer pageId = activity.getPageId();
		if (pageId != null) {
			activityNameChangeNoticeQueueService.push(activity.getId());
		}
		// 订阅活动通知发送
		activityIsAboutToStartQueueService.add(activity);
		// 活动封面url同步
		activityCoverUrlSyncQueueService.push(activity.getId());
		Integer signId = activity.getSignId();
		if (signId != null) {
			BigDecimal integralValue = activity.getIntegralValue();
			integralValue = Optional.ofNullable(integralValue).orElse(BigDecimal.valueOf(0));
			oldIntegralValue = Optional.ofNullable(oldIntegralValue).orElse(BigDecimal.valueOf(0));
			if (integralValue.compareTo(oldIntegralValue) != 0) {
				activityIntegralChangeQueueService.add(signId);
				// 机构用户统计中用户获得的积分更新
				userStatSummaryService.updateActivityUserIntegral(activity.getId(), activity.getIntegralValue());
			}
		}
		if (oldActivity != null) {
			// 提醒已收藏、已报名的用户活动的变更，需要判断的变更内容：活动地点、活动时间
			boolean activityDataChange = !Objects.equals(activity.getAddress(), oldActivity.getAddress()) || !Objects.equals(activity.getDetailAddress(), oldActivity.getDetailAddress());
			LocalDateTime now = LocalDateTime.now();
			// 时间是否改变
			boolean timeChanged = activity.getStartTime().compareTo(oldActivity.getStartTime()) != 0;
			if (!timeChanged) {
				timeChanged = activity.getEndTime().compareTo(oldActivity.getEndTime()) != 0;
			}
			boolean activityEnded = activity.getEndTime().isBefore(now);
			if (timeChanged && !activityEnded) {
				activityDataChange = true;
			}
			if (activityDataChange) {
				// 给收藏活动和报名活动的用户发送通知
				activityDataChangeQueueService.add(activity.getId());
			}
		}
		// 活动定时发布
		Boolean released = activity.getReleased();
		released = Optional.ofNullable(released).orElse(false);
		if (!released) {
			Boolean timingRelease = activity.getTimingRelease();
			timingRelease = Optional.ofNullable(timingRelease).orElse(false);
			LocalDateTime timingReleaseTime = activity.getTimingReleaseTime();
			ActivityTimingReleaseQueueService.QueueParamDTO queueParam = ActivityTimingReleaseQueueService.QueueParamDTO.builder()
					.activityId(activity.getId())
					.releaseTime(timingReleaseTime)
					.loginUser(loginUser)
					.build();
			if (timingRelease) {
				Boolean timingReleaseTimeChange = true;
				if (oldActivity != null) {
					LocalDateTime oldTimingReleaseTime = oldActivity.getTimingReleaseTime();
					if (oldTimingReleaseTime != null) {
						timingReleaseTimeChange = timingReleaseTime.compareTo(oldTimingReleaseTime) != 0;
					}
				}
				if (timingReleaseTimeChange) {
					// 取消定时发布
					activityTimingReleaseQueueService.remove(queueParam);
					// 定时发布
					activityTimingReleaseQueueService.push(queueParam);
				}
			} else {
				// 取消定时发布
				activityTimingReleaseQueueService.remove(queueParam);
			}
		}
	}

	/**活动状态变更
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:50:31
	 * @param activity
	 * @return void
	*/
	public void statusChange(Activity activity) {
		Integer status = activity.getStatus();
		Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
		if (Objects.equals(Activity.StatusEnum.DELETED, statusEnum)) {
			// 活动被删除
			activityIsAboutToStartQueueService.remove(activity.getId());
			// 删除表单推送的数据
			activityDataPushQueueService.delete(activity);
		}

	}

	/**发布状态的改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 20:04:29
	 * @param activity
	 * @param loginUser
	 * @return void
	*/
	public void releaseStatusChange(Activity activity, LoginUserDTO loginUser) {
		Boolean released = activity.getReleased();
		if (released) {
			// 往表单推送数据
			activityDataPushQueueService.add(activity);
		} else {
			// 删除记录
			// 删除表单推送的数据
			activityDataPushQueueService.delete(activity);
			// 取消定时发布
			ActivityTimingReleaseQueueService.QueueParamDTO queueParam = ActivityTimingReleaseQueueService.QueueParamDTO.builder()
					.activityId(activity.getId())
					.releaseTime(activity.getTimingReleaseTime())
					.loginUser(loginUser)
					.build();
			activityTimingReleaseQueueService.remove(queueParam);
		}
		// 活动发布范围改变
		activityReleaseScopeChangeQueueService.add(activity.getId());
	}

}