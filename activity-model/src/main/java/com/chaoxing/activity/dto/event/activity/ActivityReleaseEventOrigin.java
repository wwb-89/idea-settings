package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动发布事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-24 20:29:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReleaseEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}
