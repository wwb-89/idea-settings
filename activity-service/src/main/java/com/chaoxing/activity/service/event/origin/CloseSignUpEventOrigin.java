package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**关闭报名事件源
 * @author wwb
 * @version ver 1.0
 * @className CloseSignUpEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:17:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseSignUpEventOrigin extends AbstractEventOrigin {

	private Integer activityId;

}