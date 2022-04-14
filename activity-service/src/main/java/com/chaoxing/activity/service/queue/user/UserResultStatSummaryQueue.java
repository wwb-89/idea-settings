package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserResultStatSummaryQueue
 * @description
 * @blame wwb
 * @date 2021-11-10 21:35:15
 */
@Slf4j
@Service
public class UserResultStatSummaryQueue implements IQueue<UserResultStatSummaryQueue.QueueParamDTO> {

	/** 用户成绩队列缓存key */
	private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_result_stat_summary";

	@Resource
	private RedissonClient redissonClient;

	public void push(QueueParamDTO queueParam) {
		push(redissonClient, KEY, queueParam);
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, KEY);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 用户id */
		private Integer uid;
		/** 活动id */
		private Integer activityId;

	}

}
