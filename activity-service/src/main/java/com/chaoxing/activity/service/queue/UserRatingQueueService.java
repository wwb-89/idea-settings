package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户评价队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserRatingQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 17:53:54
 */
@Slf4j
@Service
public class UserRatingQueueService implements IQueueService<UserRatingQueueService.QueueParamDTO> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_rating";

	@Resource
	private RedissonClient redissonClient;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:55:25
	 * @param queueParam
	 * @return void
	*/
	public void add(QueueParamDTO queueParam) {
		push(redissonClient, QUEUE_CACHE_KEY, queueParam);
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:56:23
	 * @param 
	 * @return com.chaoxing.activity.service.queue.UserRatingQueueService.QueueParamDTO
	*/
	public QueueParamDTO get() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 用户id */
		private Integer uid;
		/** 报名签到id */
		private Integer signId;

	}

}