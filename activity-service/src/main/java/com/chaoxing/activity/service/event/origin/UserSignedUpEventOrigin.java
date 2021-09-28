package com.chaoxing.activity.service.event.origin;

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
public class UserSignedUpEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Integer signUpId;
	private LocalDateTime signedUpTime;
	private Long timestamp;

}