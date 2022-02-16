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

/**活动参与角色通知队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityParticipateRoleNoticeQueue
 * @description 厦门定制
 * @blame wwb
 * @date 2022-02-16 16:05:37
 */
@Slf4j
@Service
public class ActivityParticipateRoleNoticeQueue implements IQueue<ActivityParticipateRoleNoticeQueue.QueueParamDTO> {

	private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "custom" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_participate_role_notice";

	@Resource
	private RedissonClient redissonClient;

	public void push(ActivityParticipateRoleNoticeQueue.QueueParamDTO queueParam) {
		push(redissonClient, KEY, queueParam);
	}

	public void delayPush(ActivityParticipateRoleNoticeQueue.QueueParamDTO queueParam) {
		delayPush(redissonClient, KEY, queueParam);
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, KEY);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 活动id */
		private Integer activityId;
		/** 时间 */
		private Long timestamp;

	}

}