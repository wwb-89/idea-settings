package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**开启报名事件源
 * @author wwb
 * @version ver 1.0
 * @className OpenSignUpEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:16:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenSignUpEventOrigin extends AbstractEventOrigin {

	private Integer activityId;

}