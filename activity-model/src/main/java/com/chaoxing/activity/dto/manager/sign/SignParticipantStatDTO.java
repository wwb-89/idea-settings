package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**参与情况
 * @author wwb
 * @version ver 1.0
 * @className SignParticipantStatDTO
 * @description
 * @blame wwb
 * @date 2020-11-24 19:27:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignParticipantStatDTO {

	/** 报名id */
	private Integer signUpId;
	/** 报名开始时间 */
	private LocalDateTime signUpStartTime;
	/** 报名结束时间 */
	private LocalDateTime signUpEndTime;
	/** 参与数 */
	private Integer participateNum;
	/** 限制数 */
	private Integer limitNum;

}