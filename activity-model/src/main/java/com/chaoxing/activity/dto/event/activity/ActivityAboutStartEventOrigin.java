package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**活动即将开始数据源
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutStartEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-24 20:36:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAboutStartEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 市场id */
	private Integer marketId;
	/** 活动开始事件 */
	private LocalDateTime startTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}