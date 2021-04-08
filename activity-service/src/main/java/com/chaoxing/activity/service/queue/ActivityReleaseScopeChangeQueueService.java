package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动发布范围改变队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseScopeChangeQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 17:10:17
 */
@Slf4j
@Service
public class ActivityReleaseScopeChangeQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_release_scope_change";

	@Resource
	private RedisTemplate redisTemplate;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:12:16
	 * @param activityId
	 * @return void
	*/
	public void add(Integer activityId) {
		if (activityId != null) {
			ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
			listOperations.leftPush(QUEUE_CACHE_KEY, activityId);
		}
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:12:29
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer get() {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		return listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
	}

}