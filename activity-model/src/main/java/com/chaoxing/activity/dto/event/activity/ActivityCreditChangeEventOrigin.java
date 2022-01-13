package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动学分改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreditChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2022-01-13 17:01:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreditChangeEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 时间戳 */
	private Long timestamp;

}