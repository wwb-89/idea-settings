package com.chaoxing.activity.service.queue.user;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户总参与时长推送队列
 * @author wwb
 * @version ver 1.0
 * @className UserTotalTimeLengthQueueService
 * @description
 * @blame wwb
 * @date 2021-06-11 11:00:38
 */
@Slf4j
@Service
public class UserTotalTimeLengthQueueService implements IQueueService<UserTotalTimeLengthQueueService.QueueParamDTO> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_total_time_length";

	@Resource
	private RedissonClient redissonClient;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-11 11:02:35
	 * @param queueParam
	 * @return void
	*/
	public void push(QueueParamDTO queueParam) {
		push(redissonClient, QUEUE_CACHE_KEY, queueParam);
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-11 11:02:40
	 * @param 
	 * @return com.chaoxing.activity.service.queue.UserTotalTimeLengthQueueService.QueueParamDTO
	*/
	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 用户id */
		private Integer uid;

	}

}