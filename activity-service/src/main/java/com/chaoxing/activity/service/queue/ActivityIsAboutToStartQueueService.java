package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**活动即将开始队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityIsAboutToStartQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 16:32:36
 */
@Slf4j
@Service
public class ActivityIsAboutToStartQueueService {

	/** 通知已报名用户活动即将开始队列缓存key */
	private static final String SIGNED_UP_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "is_about_to_start" + CacheConstant.CACHE_KEY_SEPARATOR + "signed_up";

	@Resource
	private RedisTemplate redisTemplate;

	/**队列添加数据
	 * @Description 已发布的活动才通知
	 * @author wwb
	 * @Date 2021-03-26 16:37:01
	 * @param activity
	 * @return void
	*/
	public void add(Activity activity) {
		Boolean released = activity.getReleased();
		released = Optional.ofNullable(released).orElse(Boolean.FALSE);
		if (!released) {
			return;
		}
		LocalDateTime now = LocalDateTime.now();
		long nowTimestamp = DateUtils.date2Timestamp(now);
		long startTimestamp = DateUtils.date2Timestamp(activity.getStartTime());
		if (startTimestamp - nowTimestamp < CommonConstant.ACTIVITY_NOTICE_TIME_MILLISECOND) {
			// 小于通知阈值不处理
			return;
		}
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		// 开始前多少时间发送通知
		zSetOperations.add(SIGNED_UP_QUEUE_CACHE_KEY, activity.getId(), startTimestamp - CommonConstant.ACTIVITY_NOTICE_TIME_MILLISECOND);
	}

	/**从队列中删除数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:50:20
	 * @param activityId
	 * @return void
	*/
	public void remove(Integer activityId) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(SIGNED_UP_QUEUE_CACHE_KEY, activityId);
	}

	/**获取队列中的数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 16:41:23
	 * @param 
	 * @return org.springframework.data.redis.core.ZSetOperations.TypedTuple<java.lang.Integer>
	*/
	public ZSetOperations.TypedTuple<Integer> get() {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		Set<ZSetOperations.TypedTuple<Integer>> typedTuples = zSetOperations.rangeByScoreWithScores(SIGNED_UP_QUEUE_CACHE_KEY, 0, Long.MAX_VALUE, 0, 10);
		Iterator<ZSetOperations.TypedTuple<Integer>> iterator = typedTuples.iterator();
		while (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

}
