package com.chaoxing.activity.service.event.origin;

import lombok.Data;

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
public class ActivityAboutStartEventOrigin {

	private Integer activityId;
	private Integer marketId;
	private LocalDateTime startTime;

}