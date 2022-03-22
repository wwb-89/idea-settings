package com.chaoxing.activity.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**报名签到统计汇总
 * @author wwb
 * @version ver 1.0
 * @className SignStatSummaryDTO
 * @description
 * @blame wwb
 * @date 2022-03-22 11:09:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignStatSummaryDTO {

	/** 报名签到Id */
	private Integer signId;
	/** 签到数量 */
	private Integer signedInNum;
	/** 签到率 */
	private BigDecimal signInRate;
	/** 报名人数 */
	private Integer signedUpNum;
	/** 平均参与时长 */
	private Integer avgParticipateTimeLength;

}