package com.chaoxing.activity.service.event.origin;

import lombok.Data;

/**删除活动事件源
 * @author wwb
 * @version ver 1.0
 * @className DeleteActivityEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-24 20:35:26
 */
@Data
public class DeleteActivityEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 市场id */
	private Integer marketId;
	/** 机构id */
	private Integer fid;
	/** 时间戳 */
	private Long timestamp;

}