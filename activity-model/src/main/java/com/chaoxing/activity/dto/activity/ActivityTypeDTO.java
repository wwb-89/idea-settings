package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.model.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动类型
 * @author wwb
 * @version ver 1.0
 * @className ActivityTypeDTO
 * @description
 * @blame wwb
 * @date 2020-11-13 17:48:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTypeDTO {

	private String name;
	private String value;

	public static ActivityTypeDTO buildFromActivityTypeEnum(Activity.ActivityTypeEnum activityTypeEnum) {
		return ActivityTypeDTO.builder()
				.name(activityTypeEnum.getName())
				.value(activityTypeEnum.getValue())
				.build();
	}

}
