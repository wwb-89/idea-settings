package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
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
public class ActivityStartEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}