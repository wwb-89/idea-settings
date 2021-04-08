package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
public class ActivityCoverUrlSyncQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_cover_url_sync";

	@Resource
	private RedisTemplate redisTemplate;

	/**新增队列数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:17:14
	 * @param activityId
	 * @return void
	*/
	public void add(@NotNull Integer activityId) {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(QUEUE_CACHE_KEY, activityId);
	}

	/**获取队列数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:18:08
	 * @param 
	 * @return java.lang.Integer 活动id
	*/
	public Integer get() {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		return listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
	}

}