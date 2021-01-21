package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.activity.ActivityCoverUrlSyncDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**活动封面服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverService
 * @description
 * @blame wwb
 * @date 2021-01-20 10:40:27
 */
@Slf4j
@Service
public class ActivityCoverService {

	/** 云盘图片url */
	private static final String IMAGE_URL_PREFIX = "http://p.ananas.chaoxing.com/star3/origin/";
	/** 活动封面url同步队列缓存key */
	private static final String ACTIVITY_COVER_URL_SYNC_QUEUE_CACHE_KEY = CacheConstant.CACHE_KEY_PREFIX + "activity_cover" + CacheConstant.CACHE_KEY_SEPARATOR + "sync_queue";

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private ActivityMapper activityMapper;
	@Resource
	private CloudApiService cloudApiService;

	/**通知更新封面
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 10:41:14
	 * @param activityId
	 * @param cloudId
	 * @return void
	*/
	public void noticeUpdateCoverUrl(Integer activityId, String cloudId) {
		ListOperations<String, ActivityCoverUrlSyncDTO> listOperations = redisTemplate.opsForList();
		ActivityCoverUrlSyncDTO activityCoverUrlSync = ActivityCoverUrlSyncDTO.builder().activityId(activityId).cloudId(cloudId).build();
		listOperations.leftPush(ACTIVITY_COVER_URL_SYNC_QUEUE_CACHE_KEY, activityCoverUrlSync);
	}

	/**同步活动封面url
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 11:07:09
	 * @param 
	 * @return void
	*/
	public void syncActivityCoverUrl() {
		ListOperations<String, ActivityCoverUrlSyncDTO> listOperations = redisTemplate.opsForList();
		Long size = listOperations.size(ACTIVITY_COVER_URL_SYNC_QUEUE_CACHE_KEY);
		size = Optional.ofNullable(size).orElse(0L);
		if (size < 1) {
			return;
		}
		for (int i = 0; i < size; i++) {
			ActivityCoverUrlSyncDTO activityCoverUrlSync = listOperations.rightPop(ACTIVITY_COVER_URL_SYNC_QUEUE_CACHE_KEY);
			if (activityCoverUrlSync != null) {
				String cloudId = activityCoverUrlSync.getCloudId();
				String imageUrl = cloudApiService.getImageUrl(cloudId);
				if (StringUtils.isNotBlank(imageUrl)) {
					updateActivityCoverUrl(activityCoverUrlSync.getActivityId(), imageUrl);
				}
			}
		}
	}

	/**更新活动封面
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 11:22:17
	 * @param activityId
	 * @param coverUrl
	 * @return void
	*/
	public void updateActivityCoverUrl(Integer activityId, String coverUrl) {
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getCoverUrl, coverUrl)
		);
	}

	/**获取封面url
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 11:24:34
	 * @param activity
	 * @return java.lang.String
	*/
	public String getCoverUrl(Activity activity) {
		String coverUrl = activity.getCoverUrl();
		if (StringUtils.isNotBlank(coverUrl)) {
			return coverUrl;
		}
		String coverCloudId = activity.getCoverCloudId();
		if (StringUtils.isNotBlank(coverCloudId)) {
			return IMAGE_URL_PREFIX + coverCloudId;
		}
		return "";
	}

}