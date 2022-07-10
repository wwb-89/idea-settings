package com.chaoxing.activity.dto.event.sign;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**报名新增事件源
 * @author wwb
 * @version ver 1.0
 * @className SignUpAddEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:16:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAddEventOrigin extends AbstractEventOrigin {

	private Integer signId;
	private Integer signUpId;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}