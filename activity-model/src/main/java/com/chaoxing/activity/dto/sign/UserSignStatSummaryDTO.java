package com.chaoxing.activity.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户报名签到统计汇总
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryDTO
 * @description
 * @blame wwb
 * @date 2021-05-27 11:07:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignStatSummaryDTO {

	/** 用户id */
	private Integer uid;
	/** 报名签到id */
	private Integer signId;
	/** 报名数量 */
	private Integer signUpNum;
	/** 报名成功数量 */
	private Integer signedUpNum;
	/** 报名时间 */
	private LocalDateTime signUpTime;
	/** 签到数量 */
	private Integer signInNum;
	/** 签到成功数量 */
	private Integer signedInNum;
	/** 参数时长 */
	private Integer participateTimeLength;

}
