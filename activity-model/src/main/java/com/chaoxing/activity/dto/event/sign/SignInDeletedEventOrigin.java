package com.chaoxing.activity.dto.event.sign;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**签到删除事件源
 * @author wwb
 * @version ver 1.0
 * @className SignInDeletedEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:37:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInDeletedEventOrigin extends AbstractEventOrigin {

	private Integer signId;
	private Integer signInId;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}