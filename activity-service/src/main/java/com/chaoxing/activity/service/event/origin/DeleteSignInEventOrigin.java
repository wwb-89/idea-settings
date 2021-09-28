package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**删除签到事件源
 * @author wwb
 * @version ver 1.0
 * @className DeleteSignInEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:37:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSignInEventOrigin {

	private Integer activityId;
	private Integer signInId;
	private Long timestamp;

}