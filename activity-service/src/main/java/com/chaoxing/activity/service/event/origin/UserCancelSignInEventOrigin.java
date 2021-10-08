package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户取消签到事件源
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignInEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:44:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCancelSignInEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signInId;

}