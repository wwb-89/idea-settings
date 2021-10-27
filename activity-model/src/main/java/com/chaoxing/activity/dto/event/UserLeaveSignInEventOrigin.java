package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户请假事件源
 * @author wwb
 * @version ver 1.0
 * @className UserLeaveSignInEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 14:50:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLeaveSignInEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signInId;
	private LocalDateTime leaveTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}