package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户取消签退（未签）事件源
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignOutEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 14:54:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCancelSignOutEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signOutId;
	private Long timestamp;

}