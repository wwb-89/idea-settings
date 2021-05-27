package com.chaoxing.activity.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
	/** 有效的（报名有效）报名数 */
	private Integer validSignUpNum;
	/** 报名数 */
	private Integer signedUpNum;
	/** 有效的（签到有效）签到数 */
	private Integer validSignedInNum;
	/** 签到数 */
	private Integer signedInNum;
	/** 签到率 */
	private BigDecimal signedInRate;
	/** 参数时长 */
	private Integer participateTimeLength;

}
