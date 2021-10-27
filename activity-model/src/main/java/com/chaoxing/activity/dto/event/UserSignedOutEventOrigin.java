package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户签退事件源
 * @author wwb
 * @version ver 1.0
 * @className UserSignedOutEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 14:52:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedOutEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signOutId;
	private LocalDateTime signedOutTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}