package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:06:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEndEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 旧状态 */
	private Integer oldStatus;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}