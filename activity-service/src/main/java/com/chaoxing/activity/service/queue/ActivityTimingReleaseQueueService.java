package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**活动定时发布队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimingReleaseQueueService
 * @description
 * @blame wwb
 * @date 2021-06-08 10:00:10
 */
@Slf4j
@Service
public class ActivityTimingReleaseQueueService implements IDelayedQueueService<ActivityTimingReleaseQueueService.QueueParamDTO> {

	private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_timing_release";

	@Resource
	private RedissonClient redissonClient;

	public void push(QueueParamDTO queueParam) {
		push(redissonClient, CACHE_KEY, queueParam, queueParam.getReleaseTime());
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, CACHE_KEY);
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
