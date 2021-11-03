package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
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
public class ActivityAboutEndEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 结束时间 */
	private LocalDateTime endTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}