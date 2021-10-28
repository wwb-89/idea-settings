package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户成功签到事件源
 * @author wwb
 * @version ver 1.0
 * @className UserSignedInEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:43:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedInEventOrigin extends AbstractEventOrigin {

	private Integer signIn;
	private Integer signInId;
	private Integer uid;
	private LocalDateTime signedInTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}