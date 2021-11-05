package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
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
public class UserCancelSignUpEventOrigin extends AbstractEventOrigin {

	private Integer signId;
	private Integer signUpId;
	private Integer uid;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}