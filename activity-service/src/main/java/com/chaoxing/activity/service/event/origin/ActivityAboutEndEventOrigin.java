package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**活动即将结束事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutEndEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:03:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAboutEndEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 结束事件 */
	private LocalDateTime endTime;
	/** 时间戳 */
	private Long timestamp;

}