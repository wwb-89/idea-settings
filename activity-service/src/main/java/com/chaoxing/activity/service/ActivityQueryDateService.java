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

	/**活动筛选的时间条件
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 22:10:09
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.dto.ActivityQueryDateDTO>
	*/
	public List<ActivityQueryDateDTO> listAll() {
		List<ActivityQueryDateDTO> activityQueryDates = new ArrayList<>();
		ActivityQueryDateScopeEnum[] values = ActivityQueryDateScopeEnum.values();
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