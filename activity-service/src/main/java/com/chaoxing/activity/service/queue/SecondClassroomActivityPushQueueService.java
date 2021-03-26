package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**第二课堂活动推送队列服务
 * @author wwb
 * @version ver 1.0
 * @className SecondClassroomActivityPushQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 16:58:43
 */
@Slf4j
@Service
public class SecondClassroomActivityPushQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "second_classroom_activity_push";

	@Resource
	private RedisTemplate redisTemplate;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:01:58
	 * @param activity
	 * @return void
	*/
	public void add(Activity activity) {
		Integer secondClassroomFlag = activity.getSecondClassroomFlag();
		if (Objects.equals(secondClassroomFlag, 1)) {
			add(activity.getId());
		}
	}

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:48:46
	 * @param activityId
	 * @return void
	*/
	public void add(Integer activityId) {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(QUEUE_CACHE_KEY, activityId);
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:02:18
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer get() {
		ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
		return listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
	}

}