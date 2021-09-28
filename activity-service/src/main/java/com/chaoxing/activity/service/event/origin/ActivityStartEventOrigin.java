package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**活动开始事件来源
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:00:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStartEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 开始事件 */
	private LocalDateTime startTime;
	/** 时间戳 */
	private Long timestamp;

}