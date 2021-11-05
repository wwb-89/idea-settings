package com.chaoxing.activity.dto.event.sign;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**报名即将结束事件源
 * @author wwb
 * @version ver 1.0
 * @className SignUpAboutEndEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:35:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAboutEndEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer signUpId;
	private String signUpName;
	private LocalDateTime signUpEndTime;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}