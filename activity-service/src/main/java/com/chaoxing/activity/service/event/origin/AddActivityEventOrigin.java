package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**新增活动事件源
 * @author wwb
 * @version ver 1.0
 * @className AddActivityEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-24 20:22:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddActivityEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;
	/** 市场id */
	private Integer marketId;
	/** 机构id */
	private Integer fid;
	/** 创建人uid */
	private Integer uid;

}