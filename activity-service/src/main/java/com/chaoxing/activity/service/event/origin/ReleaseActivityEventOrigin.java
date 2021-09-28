package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**发布活动事件源
 * @author wwb
 * @version ver 1.0
 * @className ReleaseActivityEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-24 20:29:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseActivityEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 市场id */
	private Integer marketId;
	/** 机构id */
	private Integer fid;
	/** 时间戳 */
	private Long timestamp;

}
