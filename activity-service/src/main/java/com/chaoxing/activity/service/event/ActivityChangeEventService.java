package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.data.DataPushService;
import com.chaoxing.activity.service.manager.bigdata.BigDataPointApiService;
import com.chaoxing.activity.service.queue.BigDataPointQueueService;
import com.chaoxing.activity.service.queue.BigDataPointTaskQueueService;
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
	private ActivityIntegralChangeQueueService activityIntegralChangeQueueService;
	@Resource
	private ActivityDataChangeQueueService activityDataChangeQueueService;
	@Resource
	private UserStatSummaryHandleService userStatSummaryService;
	@Resource
	private ActivityTimingReleaseQueueService activityTimingReleaseQueueService;
	@Resource
	private ActivityWorkInfoSyncQueueService activityWorkInfoSyncQueueService;
	@Resource
	private DataPushService dataPushService;
	@Resource
	private BigDataPointTaskQueueService bigDataPointTaskQueueService;
	@Resource
	private BigDataPointQueueService bigDataPointQueueService;

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
		Integer activityId = activity.getId();
		Integer createFid = activity.getCreateFid();
		// 数据推送
		dataPushService.dataPush(new DataPushService.DataPushParamDTO(createFid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, String.valueOf(activityId), null));
		// 订阅活动状态处理
		activityStatusUpdateQueueService.push(activity);
		// 通知门户修改网站的title
		activityNameChangeNoticeQueueService.push(activityId);
		// 订阅活动通知发送
		activityIsAboutToStartQueueService.pushNoticeSignedUp(new ActivityIsAboutToStartQueueService.QueueParamDTO(activityId, activity.getStartTime()), false);
		// 活动封面url同步
		activityCoverUrlSyncQueueService.push(activityId);
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
				activityDataChangeQueueService.add(activityId);
			}
			Integer signId = activity.getSignId();
			if (signId != null) {
				BigDecimal integral = Optional.ofNullable(activity.getIntegral()).orElse(BigDecimal.valueOf(0));
				BigDecimal oldIntegral = Optional.ofNullable(oldActivity.getIntegral()).orElse(BigDecimal.valueOf(0));
				if (integral.compareTo(oldIntegral) != 0) {
					activityIntegralChangeQueueService.push(signId);
					// 机构用户统计中用户获得的积分更新
					userStatSummaryService.updateActivityUserIntegral(activity.getId(), activity.getIntegral());
				}
			}
		}
		// 更新作品征集信息
		activityWorkInfoSyncQueueService.push(activityId);
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
					activityTimingReleaseQueueService.remove(activityId);
					// 定时发布
					activityTimingReleaseQueueService.push(queueParam);
				}
			} else {
				// 取消定时发布
				activityTimingReleaseQueueService.remove(activityId);
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
			// 活动数据推送
			Integer activityId = activity.getId();
			Integer createFid = activity.getCreateFid();
			dataPushService.dataPush(new DataPushService.DataPushParamDTO(createFid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, String.valueOf(activityId), null));
			// 大数据积分（举办活动）
			bigDataPointQueueService.push(new BigDataPointQueueService.QueueParamDTO(activity.getCreateUid(), activity.getCreateFid(), activityId, BigDataPointApiService.PointTypeEnum.CANCEL_ORGANIZE_ACTIVITY.getValue()));
		}else if (Objects.equals(Activity.StatusEnum.ENDED, statusEnum)) {
			// 活动结束，大数据积分推送
			BigDataPointTaskQueueService.QueueParamDTO queueParam = new BigDataPointTaskQueueService.QueueParamDTO(activity.getId(), activity.getCreateFid(), true);
			bigDataPointTaskQueueService.push(queueParam);
		} else if (Objects.equals(Activity.StatusEnum.ONGOING, statusEnum)) {
			// 进行中，删除大数据已推送的积分
			BigDataPointTaskQueueService.QueueParamDTO queueParam = new BigDataPointTaskQueueService.QueueParamDTO(activity.getId(), activity.getCreateFid(), false);
			bigDataPointTaskQueueService.push(queueParam);
		}
		activityIsAboutToStartQueueService.pushNoticeSignedUp(new ActivityIsAboutToStartQueueService.QueueParamDTO(activity.getId(), activity.getStartTime()), false);
	}

	/**发布状态的改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 20:04:29
	 * @param activity
	 * @return void
	*/
	public void releaseStatusChange(Activity activity) {
		Integer activityId = activity.getId();
		Boolean released = activity.getReleased();
		if (!released) {
			// 取消定时发布
			activityTimingReleaseQueueService.remove(activityId);
			// 大数据积分（举办活动）
			bigDataPointQueueService.push(new BigDataPointQueueService.QueueParamDTO(activity.getCreateUid(), activity.getCreateFid(), activityId, BigDataPointApiService.PointTypeEnum.CANCEL_ORGANIZE_ACTIVITY.getValue()));
		} else {
			// 大数据积分（举办活动）
			bigDataPointQueueService.push(new BigDataPointQueueService.QueueParamDTO(activity.getCreateUid(), activity.getCreateFid(), activityId, BigDataPointApiService.PointTypeEnum.ORGANIZE_ACTIVITY.getValue()));
		}
		// 活动发布范围改变
		activityReleaseScopeChangeQueueService.push(activityId);
		Integer createFid = activity.getCreateFid();
		dataPushService.dataPush(new DataPushService.DataPushParamDTO(createFid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, String.valueOf(activityId), null));
	}

}