package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

/**活动状态更新队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusUpdateQueueService
 * @description
 * 新增/修改活动的时候将活动开始时间和结束书剑推送到队列中，新开定时任务去监听， 当时间到了就更新活动的状态
 * @blame wwb
 * @date 2021-03-26 13:57:11
 */
@Slf4j
@Service
public class ActivityStatusUpdateQueueService {

	/** 活动开始时间队列缓存key */
	private static final String ACTIVITY_START_TIME_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_start_time";
	/** 活动开始时间队列缓存key */
	private static final String ACTIVITY_END_TIME_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_end_time";

	@Resource
	private RedisTemplate redisTemplate;

	/**添加
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:56:34
	 * @param activity
	 * @return void
	*/
	public void addTime(Activity activity) {
		addStartTime(activity.getId(), activity.getStartTime());
		addEndTime(activity.getId(), activity.getEndTime());
	}
	/**将活动开始时间放入活动状态更新队列中
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:19:09
	 * @param activityId
	 * @param startTime
	 * @return void
	*/
	private void addStartTime(Integer activityId, LocalDateTime startTime) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.add(ACTIVITY_START_TIME_QUEUE_CACHE_KEY, activityId, DateUtils.date2Timestamp(startTime));
	}

	/**移除开始时间队列
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:45:19
	 * @param activityId
	 * @return void
	*/
	public void removeStartTime(Integer activityId) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(ACTIVITY_START_TIME_QUEUE_CACHE_KEY, activityId);
	}

	/**将活动结束时间放入活动状态更新队列中
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 14:19:09
	 * @param activityId
	 * @param endTime
	 * @return void
	 */
	private void addEndTime(Integer activityId, LocalDateTime endTime) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.add(ACTIVITY_END_TIME_QUEUE_CACHE_KEY, activityId, DateUtils.date2Timestamp(endTime));
	}

	/**移除开始时间队列
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 14:45:19
	 * @param activityId
	 * @return void
	 */
	public void removeEndTime(Integer activityId) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(ACTIVITY_END_TIME_QUEUE_CACHE_KEY, activityId);
	}

	/**获取最近的开始时间数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:36:27
	 * @param 
	 * @return org.springframework.data.redis.core.ZSetOperations.TypedTuple<java.lang.Integer>
	*/
	public ZSetOperations.TypedTuple<Integer> getStartTimeQueueData() {
		return getQueueData(ACTIVITY_START_TIME_QUEUE_CACHE_KEY);
	}

	/**获取最近的结束时间数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:36:37
	 * @param 
	 * @return org.springframework.data.redis.core.ZSetOperations.TypedTuple<java.lang.Integer>
	*/
	public ZSetOperations.TypedTuple<Integer> getEndTimeQueueData() {
		return getQueueData(ACTIVITY_END_TIME_QUEUE_CACHE_KEY);
	}

	private ZSetOperations.TypedTuple<Integer> getQueueData(String cacheKey) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		Set<ZSetOperations.TypedTuple<Integer>> typedTuples = zSetOperations.rangeByScoreWithScores(cacheKey, 0, Long.MAX_VALUE, 0, 10);
		Iterator<ZSetOperations.TypedTuple<Integer>> iterator = typedTuples.iterator();
		while (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

}