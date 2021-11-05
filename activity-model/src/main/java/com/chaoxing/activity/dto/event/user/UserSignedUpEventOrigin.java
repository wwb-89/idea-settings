package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户成功报名事件源
 * @author wwb
 * @version ver 1.0
 * @className UserSignedUpEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:39:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedUpEventOrigin extends AbstractEventOrigin {

	private Integer signId;
	private Integer signUpId;
	private Integer uid;
	private LocalDateTime signedUpTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}