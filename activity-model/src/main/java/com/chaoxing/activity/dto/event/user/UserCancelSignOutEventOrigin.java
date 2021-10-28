package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
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
public class UserCancelSignOutEventOrigin extends AbstractEventOrigin {

	private Integer signId;
	private Integer signOutId;
	private Integer uid;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}