package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.model.ActivityComponentValue;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**活动组件值对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityComponentValueDTO
 * @description
 * @blame wwb
 * @date 2021-07-13 15:30:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityComponentValueDTO {

	/** 主键 */
	private Integer id;
	/** 活动id */
	private Integer activityId;
	/** 模版组件id */
	private Integer templateComponentId;
	/** 模版id */
	private Integer templateId;
	/** 组件id */
	private Integer componentId;
	/** 值 */
	private String value;
	/** 云盘id集合 */
	private String cloudIds;
	/** 模板组件名称 */
	private String templateComponentName;

	public ActivityComponentValue buildActivityComponentValue() {
		ActivityComponentValue activityComponentValue = new ActivityComponentValue();
		BeanUtils.copyProperties(this, activityComponentValue);
		return activityComponentValue;
	}

	public static List<ActivityComponentValue> buildActivityComponentValues(List<ActivityComponentValueDTO> activityComponentValueDtos) {
		if (CollectionUtils.isEmpty(activityComponentValueDtos)) {
			return Lists.newArrayList();
		}
		return activityComponentValueDtos.stream().map(ActivityComponentValueDTO::buildActivityComponentValue).collect(Collectors.toList());
	}

	public static ActivityComponentValueDTO buildFromActivityComponentValue(ActivityComponentValue activityComponentValue) {
		ActivityComponentValueDTO activityComponentValueDto = new ActivityComponentValueDTO();
		BeanUtils.copyProperties(activityComponentValue, activityComponentValueDto);
		return activityComponentValueDto;
	}

	public static List<ActivityComponentValueDTO> buildFromActivityComponentValues(List<ActivityComponentValue> activityComponentValues) {
		if (CollectionUtils.isEmpty(activityComponentValues)) {
			return Lists.newArrayList();
		}
		return activityComponentValues.stream().map(ActivityComponentValueDTO::buildFromActivityComponentValue).collect(Collectors.toList());
	}

}
