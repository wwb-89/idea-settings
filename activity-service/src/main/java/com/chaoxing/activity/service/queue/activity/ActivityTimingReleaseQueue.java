package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**活动定时发布队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimingReleaseQueue
 * @description
 * @blame wwb
 * @date 2021-06-08 10:00:10
 */
@Slf4j
@Service
public class ActivityTimingReleaseQueue implements IDelayedQueue<ActivityTimingReleaseQueue.QueueParamDTO> {

	private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_timing_release";

	@Resource
	private RedissonClient redissonClient;

	public void push(QueueParamDTO queueParam) {
		remove(queueParam.getActivityId());
		push(redissonClient, CACHE_KEY, queueParam, queueParam.getReleaseTime());
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, CACHE_KEY);
	}

	public void remove(Integer activityId) {
		List<QueueParamDTO> queueParams = list(redissonClient, CACHE_KEY);
		if (CollectionUtils.isEmpty(queueParams)) {
			return;
		}
		for (QueueParamDTO queueParam : queueParams) {
			if (Objects.equals(queueParam.getActivityId(), activityId)) {
				remove(redissonClient, CACHE_KEY, queueParam);
			}
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 活动id */
		private Integer activityId;
		/** 活动发布时间 */
		private LocalDateTime releaseTime;
		/** 登录用户 */
		private LoginUserDTO loginUser;

	}

}
