package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**活动封面地址同步队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverUrlSyncQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 16:09:43
 */
@Slf4j
@Service
public class ActivityCoverUrlSyncQueueService implements IQueueService<Integer> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_cover_url_sync";

	@Resource
	private RedissonClient redissonClient;

	/**新增队列数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:17:14
	 * @param activityId
	 * @return void
	*/
	public void push(@NotNull Integer activityId) {
		push(redissonClient, QUEUE_CACHE_KEY, activityId);
	}

	/**获取队列数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:18:08
	 * @param 
	 * @return java.lang.Integer 活动id
	*/
	public Integer pop() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

}