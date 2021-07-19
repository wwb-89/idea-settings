package com.chaoxing.activity.service.util;

import com.chaoxing.activity.dto.activity.ActivityExternalDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className Model2DtoService
 * @description
 * @blame wwb
 * @date 2020-12-02 22:48:24
 */
@Slf4j
@Service
public class Model2DtoService {

	@Resource
	private ClassifyQueryService classifyQueryService;
	@Resource
	private ActivityCoverUrlSyncService activityCoverService;

	/**活动对象转换成dto
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 22:49:04
	 * @param activities
	 * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityExternalDTO>
	*/
	public List<ActivityExternalDTO> activity2Dto(List<Activity> activities) {
		List<ActivityExternalDTO> activityExternals = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(activities)) {
			// 查询活动分类列表
			List<Integer> activityClassifyIds = activities.stream().map(Activity::getActivityClassifyId).collect(Collectors.toList());
			List<Classify> classifies = classifyQueryService.listByIds(activityClassifyIds);
			Map<Integer, Classify> idClassifyMap;
			if (CollectionUtils.isNotEmpty(classifies)) {
				idClassifyMap = classifies.stream().collect(Collectors.toMap(Classify::getId, v -> v, (v1, v2) -> v2));
			} else {
				idClassifyMap = Maps.newHashMap();
			}
			for (Activity activity : activities) {
				ActivityExternalDTO activityExternal = new ActivityExternalDTO();
				BeanUtils.copyProperties(activity, activityExternal);
				Integer activityClassifyId = activityExternal.getActivityClassifyId();
				// 处理活动形式
				String activityType = activityExternal.getActivityType();
				Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(activityType);
				activityExternal.setActivityType(Optional.ofNullable(activityTypeEnum).map(Activity.ActivityTypeEnum::getName).orElse(""));
				// 处理活动分类名
				Classify classify = idClassifyMap.get(activityClassifyId);
				activityExternal.setActivityClassify(Optional.ofNullable(classify).map(Classify::getName).orElse(""));
				activityExternals.add(activityExternal);
				// 封面地址
				activityExternal.setCoverUrl(activityCoverService.getCoverUrl(activity));
				// 访问地址
				activityExternal.setUrl(activity.getPreviewUrl());
			}
		}
		return activityExternals;
	}

	/**将activity转换为ActivityExternalDTO
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-06 19:15:44
	 * @param activity
	 * @return com.chaoxing.activity.dto.activity.ActivityExternalDTO
	*/
	public ActivityExternalDTO activity2Dto(Activity activity) {
		// 查询活动分类列表
		Classify classify = classifyQueryService.getById(activity.getActivityClassifyId());
		ActivityExternalDTO activityExternal = new ActivityExternalDTO();
		BeanUtils.copyProperties(activity, activityExternal);
		// 处理活动形式
		String activityType = activityExternal.getActivityType();
		Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(activityType);
		activityExternal.setActivityType(Optional.ofNullable(activityTypeEnum).map(Activity.ActivityTypeEnum::getName).orElse(""));
		// 处理活动分类名
		activityExternal.setActivityClassify(Optional.ofNullable(classify).map(Classify::getName).orElse(""));
		// 封面地址
		activityExternal.setCoverUrl(activityCoverService.getCoverUrl(activity));
		// 访问地址
		activityExternal.setUrl(activity.getPreviewUrl());
		return activityExternal;
	}

}
