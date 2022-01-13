package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动学时改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityPeriodChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2022-01-13 17:00:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPeriodChangeEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 时间戳 */
	private Long timestamp;

}