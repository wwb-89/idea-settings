package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户取消报名事件源
 * @author wwb
 * @version ver 1.0
 * @className UserCancelSignUpEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:41:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCancelSignUpEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signUpId;
	private Long timestamp;

}