package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动发布范围改变队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseScopeChangeQueueService
 * @description 目前统计作品征集刷新参与范围
 * @blame wwb
 * @date 2021-03-26 17:10:17
 */
@Slf4j
@Service
public class ActivityReleaseScopeChangeQueue implements IQueue<Integer> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_release_scope_change";

	@Resource
	private RedissonClient redissonClient;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:12:16
	 * @param activityId
	 * @return void
	*/
	public void push(Integer activityId) {
		if (activityId != null) {
			push(redissonClient, QUEUE_CACHE_KEY, activityId);
		}
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:12:29
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer pop() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

}