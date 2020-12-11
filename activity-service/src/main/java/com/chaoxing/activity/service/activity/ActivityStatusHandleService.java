package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusHandleService
 * @description
 * @blame wwb
 * @date 2020-12-10 19:35:04
 */
@Slf4j
@Service
public class ActivityStatusHandleService {

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityHandleService activityHandleService;

	/**计算活动状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-10 19:36:50
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @return java.lang.Integer
	*/
	public Integer calActivityStatus(LocalDateTime startTime, LocalDateTime endTime, Integer status) {
		LocalDateTime now = LocalDateTime.now();
		boolean guessEnded = now.isAfter(endTime);
		boolean guessOnGoing = (now.isAfter(startTime) || now.isEqual(startTime)) && (now.isBefore(endTime) || now.isEqual(endTime));
		Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
		switch (statusEnum) {
			case RELEASED:
				// 已发布、进行中、已结束
			case ONGOING:
				// 已发布、进行中、已结束
			case ENDED:
				// 已发布、进行中、已结束
				if (guessEnded) {
					// 已结束
					return Activity.StatusEnum.ENDED.getValue();
				}
				if (guessOnGoing) {
					return Activity.StatusEnum.ONGOING.getValue();
				}
				return Activity.StatusEnum.RELEASED.getValue();
			default:
				return statusEnum.getValue();
		}
	}

	/**订阅状态同步
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-10 20:01:38
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return void
	*/
	public void subscibeStatusUpdate(Integer activityId, LocalDateTime startTime, LocalDateTime endTime) {
		String startKey = getStartSubscibeStatusUpdateCacheKey();
		String endKey = getEndSubscibeStatusUpdateCacheKey();
		ZSetOperations zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.add(startKey, activityId, startTime.toInstant(CommonConstant.DEFAULT_ZONEOFFSET).toEpochMilli());
		zSetOperations.add(endKey, activityId, endTime.toInstant(CommonConstant.DEFAULT_ZONEOFFSET).toEpochMilli());
	}

	public void deleteStartValue(Integer activityId) {
		String startKey = getStartSubscibeStatusUpdateCacheKey();
		ZSetOperations zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(startKey, activityId);
	}

	public void deleteEndValue(Integer activityId) {
		String endKey = getEndSubscibeStatusUpdateCacheKey();
		ZSetOperations zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(endKey, activityId);
	}

	private ZSetOperations.TypedTuple<Integer> getPendingSubscibeStatusUpdateActivityId(String cacheKey) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		Set<ZSetOperations.TypedTuple<Integer>> typedTuples = zSetOperations.rangeByScoreWithScores(cacheKey, 0, Long.MAX_VALUE, 0, 1);
		Iterator<ZSetOperations.TypedTuple<Integer>> iterator = typedTuples.iterator();
		while (iterator.hasNext()) {
			// 只有一条数据
			return iterator.next();
		}
		return null;
	}

	private String getStartSubscibeStatusUpdateCacheKey() {
		StringBuilder cacheKeyStringBuilder = new StringBuilder();
		cacheKeyStringBuilder.append(CacheConstant.CACHE_KEY_PREFIX);
		cacheKeyStringBuilder.append("status_start_queue");
		return cacheKeyStringBuilder.toString();
	}
	private String getEndSubscibeStatusUpdateCacheKey() {
		StringBuilder cacheKeyStringBuilder = new StringBuilder();
		cacheKeyStringBuilder.append(CacheConstant.CACHE_KEY_PREFIX);
		cacheKeyStringBuilder.append("status_end_queue");
		return cacheKeyStringBuilder.toString();
	}

	public void startStatusSync() {
		String startKey = getStartSubscibeStatusUpdateCacheKey();
		statusSync(startKey);
	}

	public void endStatusSync() {
		String endKey = getEndSubscibeStatusUpdateCacheKey();
		statusSync(endKey);
	}

	private void statusSync(String cacheKey) {
		ZSetOperations.TypedTuple<Integer> typedTuple = getPendingSubscibeStatusUpdateActivityId(cacheKey);
		if (typedTuple == null) {
			return;
		}
		Double score = typedTuple.getScore();
		// 判断时间是不是小于等于当前时间
		long l = score.longValue();
		LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(l), CommonConstant.DEFAULT_ZONEOFFSET);
		LocalDateTime now = LocalDateTime.now();
		if (now.compareTo(time) > -1) {
			Integer activityId = typedTuple.getValue();
			// 更新活动状态
			Activity activity = activityQueryService.getById(activityId);
			Integer status = calActivityStatus(activity.getStartTime(), activity.getEndTime(), Activity.StatusEnum.ENDED.getValue());
			activityHandleService.updateActivityStatus(activityId, status);
			ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
			zSetOperations.remove(cacheKey, activityId);
		}
	}

}
