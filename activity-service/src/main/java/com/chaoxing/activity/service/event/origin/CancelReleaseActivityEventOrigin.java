package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**取消发布活动数据源
 * @author wwb
 * @version ver 1.0
 * @className CancelReleaseActivityEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-24 20:33:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReleaseActivityEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 市场id */
	private Integer marketId;
	/** 机构id */
	private Integer fid;

}