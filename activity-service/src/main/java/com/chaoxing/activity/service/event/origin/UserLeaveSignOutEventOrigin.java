package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户签退请假事件源
 * @author wwb
 * @version ver 1.0
 * @className UserLeaveSignOutEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 14:55:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLeaveSignOutEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signInId;
	private LocalDateTime leaveTime;

}