package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动名称改变通知服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameChangeNoticeQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 15:01:17
 */
@Slf4j
@Service
public class ActivityNameChangeNoticeQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_name_change";

	@Resource
	private RedisTemplate redisTemplate;

	/**新增队列数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:07:29
	 * @param activityId
	 * @return void
	*/
	public void addActivityId(Integer activityId) {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(QUEUE_CACHE_KEY, activityId);
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:12:31
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer getActivityId() {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		return listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
	}

}