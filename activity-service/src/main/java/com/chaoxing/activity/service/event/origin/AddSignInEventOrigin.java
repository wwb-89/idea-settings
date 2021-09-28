package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**新增签到事件源
 * @author wwb
 * @version ver 1.0
 * @className AddSignInEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:36:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSignInEventOrigin {

	private Integer activityId;
	private Integer signInId;
	private Long timestamp;

}