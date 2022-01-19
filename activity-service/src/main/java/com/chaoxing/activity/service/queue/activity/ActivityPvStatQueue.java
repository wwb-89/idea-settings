package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动pv统计队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityPvStatQueue
 * @description
 * @blame wwb
 * @date 2022-01-19 15:45:56
 */
@Slf4j
@Service
public class ActivityPvStatQueue implements IQueue<ActivityPvStatQueue.QueueParamDTO> {

	/** 签到、签到率 */
	private static final String SIGN_IN_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_pv_stat";

	@Resource
	private RedissonClient redissonClient;

	public void push(QueueParamDTO queueParam) {
		push(redissonClient, SIGN_IN_CACHE_KEY, queueParam);
	}

	public void delayPush(QueueParamDTO queueParam) {
		delayPush(redissonClient, SIGN_IN_CACHE_KEY, queueParam);
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, SIGN_IN_CACHE_KEY);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 活动id */
		private Integer activityId;
		/** websiteId */
		private Integer websiteId;

	}

}