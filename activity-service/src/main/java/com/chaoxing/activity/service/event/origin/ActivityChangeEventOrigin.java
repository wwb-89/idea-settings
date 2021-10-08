package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:08:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityChangeEventOrigin extends AbstractEventOrigin {

	/** 活动id */
	private Integer activityId;

}
