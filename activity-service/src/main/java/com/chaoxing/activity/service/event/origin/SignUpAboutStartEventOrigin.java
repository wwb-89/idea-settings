package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**报名即将开始
 * @author wwb
 * @version ver 1.0
 * @className SignUpAboutStartEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:33:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAboutStartEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer signUpId;
	private String signUpName;
	private LocalDateTime signUpStartTime;

}