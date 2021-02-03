package com.chaoxing.activity.service.activity;

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
	 * @param activity
	 * @return java.lang.Integer
	*/
	public Integer calActivityStatus(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime endTime = activity.getEndTime();
		LocalDateTime now = LocalDateTime.now();
		boolean guessEnded = now.isAfter(endTime);
		boolean guessOnGoing = (now.isAfter(startTime) || now.isEqual(startTime)) && (now.isBefore(endTime) || now.isEqual(endTime));
		if (guessEnded) {
			// 已结束
			return Activity.StatusEnum.ENDED.getValue();
		}
		if (activity.getReleased()) {
			// 已发布的活动才处理状态
			if (guessOnGoing) {
				return Activity.StatusEnum.ONGOING.getValue();
			} else {
				return Activity.StatusEnum.RELEASED.getValue();
			}
		} else {
			return Activity.StatusEnum.WAIT_RELEASE.getValue();
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
		zSetOperations.add(startKey, activityId, DateUtils.date2Timestamp(startTime));
		zSetOperations.add(endKey, activityId, DateUtils.date2Timestamp(endTime));
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
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		Set<ZSetOperations.TypedTuple<Integer>> typedTuples = zSetOperations.rangeByScoreWithScores(cacheKey, 0, Long.MAX_VALUE, 0, 10);
		Iterator<ZSetOperations.TypedTuple<Integer>> iterator = typedTuples.iterator();
		while (iterator.hasNext()) {
			// 只有一条数据
			ZSetOperations.TypedTuple<Integer> typedTuple = iterator.next();
			Double score = typedTuple.getScore();
			// 判断时间是不是小于等于当前时间
			long l = score.longValue();
			LocalDateTime time = DateUtils.timestamp2Date(l);
			LocalDateTime now = LocalDateTime.now();
			if (now.compareTo(time) > -1) {
				Integer activityId = typedTuple.getValue();
				// 更新活动状态
				Activity activity = activityQueryService.getById(activityId);
				Integer status = calActivityStatus(activity);
				activityHandleService.updateActivityStatus(activityId, status);
				zSetOperations.remove(cacheKey, activityId);
			}
		}
	}

}
