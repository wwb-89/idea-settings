package com.chaoxing.activity.service;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.util.enums.ActivityQueryDateScopeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityQueryDateService
 * @description
 * @blame wwb
 * @date 2020-12-02 22:00:59
 */
@Slf4j
@Service
public class ActivityQueryDateService {

	/**活动筛选的时间条件（浙图）
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 22:10:09
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.dto.ActivityQueryDateDTO>
	*/
	public List<ActivityQueryDateDTO> listZjlibDateScope() {
		List<ActivityQueryDateDTO> activityQueryDates = new ArrayList<>();
		List<ActivityQueryDateScopeEnum> values = ActivityQueryDateScopeEnum.listZjLib();
		for (ActivityQueryDateScopeEnum value : values) {
			ActivityQueryDateDTO  activityQueryDate = ActivityQueryDateDTO.builder()
					.name(value.getName())
					.value(value.getValue())
					.build();
			activityQueryDates.add(activityQueryDate);
		}
		return activityQueryDates;
	}

	/**活动筛选的时间条件（通用）
	 * @Description
	 * @author wwb
	 * @Date 2021-12-30 15:57:39
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.dto.ActivityQueryDateDTO>
	*/
	public List<ActivityQueryDateDTO> listUniversalDateScope() {
		List<ActivityQueryDateDTO> activityQueryDates = new ArrayList<>();
		List<ActivityQueryDateScopeEnum> values = ActivityQueryDateScopeEnum.listUniversal();
		for (ActivityQueryDateScopeEnum value : values) {
			ActivityQueryDateDTO  activityQueryDate = ActivityQueryDateDTO.builder()
					.name(value.getName())
					.value(value.getValue())
					.build();
			activityQueryDates.add(activityQueryDate);
		}
		return activityQueryDates;
	}

}