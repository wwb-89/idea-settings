package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
	/** 结束时间 */
	private LocalDateTime endTime;

}