package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.IDelayedQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**活动状态更新队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusUpdateQueueService
 * @description
 * 新增/修改活动的时候将活动开始时间和结束书剑推送到队列中，新开定时任务去监听， 当时间到了就更新活动的状态
 * @blame wwb
 * @date 2021-03-26 13:57:11
 */
@Slf4j
@Service
public class ActivityStatusUpdateQueueService implements IDelayedQueueService<ActivityStatusUpdateQueueService.QueueParamDTO> {

	/** 活动开始时间队列缓存key */
	private static final String START_TIME_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_start_time";
	/** 活动开始时间队列缓存key */
	private static final String END_TIME_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_end_time";

	@Resource
	private RedissonClient redissonClient;

	public void push(Activity activity) {
		Integer activityId = activity.getId();
		LocalDateTime startTime = activity.getStartTime();
		pushStartTime(new QueueParamDTO(activityId, startTime));
		LocalDateTime endTime = activity.getEndTime();
		pushEndTime(new QueueParamDTO(activityId, endTime));
	}

	public void pushStartTime(QueueParamDTO queueParam) {
		push(redissonClient, START_TIME_CACHE_KEY, queueParam, queueParam.getTime());
	}

	public QueueParamDTO popStartTime() throws InterruptedException {
		return pop(redissonClient, START_TIME_CACHE_KEY);
	}

	public void pushEndTime(QueueParamDTO queueParam) {
		push(redissonClient, END_TIME_CACHE_KEY, queueParam, queueParam.getTime());
	}

	public QueueParamDTO popEndTime() throws InterruptedException {
		return pop(redissonClient, END_TIME_CACHE_KEY);
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