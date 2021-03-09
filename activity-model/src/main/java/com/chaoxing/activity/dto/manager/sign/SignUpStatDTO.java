package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**报名统计信息
 * @author wwb
 * @version ver 1.0
 * @className SignUpStatDTO
 * @description
 * @blame wwb
 * @date 2021-03-09 15:44:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpStatDTO {

	/** 报名id */
	private Integer id;
	/** 报名开始时间 */
	private LocalDateTime signUpStartTime;
	/** 报名结束时间 */
	private LocalDateTime signUpEndTime;
	/** 参与数 */
	private Integer signedUpNum;
	/** 限制数 */
	private Integer limitNum;
	/** 是否公开报名名单 */
	private Boolean publicList;

}