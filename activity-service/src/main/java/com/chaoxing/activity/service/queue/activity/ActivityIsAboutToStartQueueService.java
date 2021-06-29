package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.IDelayedQueueService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**活动即将开始队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIsAboutToStartQueueService
 * @description 活动要开始的时候通知已经报名的用户活动要开始了
 * @blame wwb
 * @date 2021-03-26 16:32:36
 */
@Slf4j
@Service
public class ActivityIsAboutToStartQueueService implements IDelayedQueueService<ActivityIsAboutToStartQueueService.QueueParamDTO> {

	/** 通知已报名用户活动即将开始队列缓存key */
	private static final String SIGNED_UP_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "is_about_to_start" + CacheConstant.CACHE_KEY_SEPARATOR + "signed_up";

	@Resource
	private RedissonClient redissonClient;
	@Resource
	private ActivityQueryService activityQueryService;

	public void pushNoticeSignedUp(QueueParamDTO queueParam, boolean direct) {
		Integer activityId = queueParam.getActivityId();
		Activity activity = activityQueryService.getById(queueParam.getActivityId());
		boolean needPush = isNeedPush(activity, direct);
		if (needPush) {
			push(redissonClient, SIGNED_UP_QUEUE_CACHE_KEY, queueParam, DateUtils.timestamp2Date(DateUtils.date2Timestamp(activity.getStartTime()) - CommonConstant.ACTIVITY_BEFORE_START_NOTICE_TIME_THRESHOLD));
		} else {
			removeNoticeSignedUp(activityId);
		}
	}

	private boolean isNeedPush(Activity activity, boolean direct) {
		if (activity == null) {
			return false;
		}
		Integer status = activity.getStatus();
		if (!Objects.equals(Activity.StatusEnum.RELEASED.getValue(), status)) {
			return false;
		}
		if (direct) {
			return true;
		}
		long nowTimestamp = DateUtils.date2Timestamp(LocalDateTime.now());
		long startTimestamp = DateUtils.date2Timestamp(activity.getStartTime());
		if (startTimestamp - nowTimestamp < CommonConstant.ACTIVITY_BEFORE_START_NOTICE_TIME_THRESHOLD) {
			// 小于通知阈值不处理
			return false;
		}
		return true;
	}

	public QueueParamDTO popNoticeSignedUp() throws InterruptedException {
		return pop(redissonClient, SIGNED_UP_QUEUE_CACHE_KEY);
	}

	public void removeNoticeSignedUp(Integer activityId) {
		remove(redissonClient, SIGNED_UP_QUEUE_CACHE_KEY, new QueueParamDTO(activityId, LocalDateTime.now()));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class QueueParamDTO {

		/** 活动id */
		@EqualsAndHashCode.Include
		private Integer activityId;
		/** 时间 */
		private LocalDateTime time;
	}

}
