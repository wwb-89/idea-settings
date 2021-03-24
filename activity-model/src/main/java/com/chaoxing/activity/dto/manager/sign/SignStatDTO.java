package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**报名签到统计对象
 * @author wwb
 * @version ver 1.0
 * @className SignStatDTO
 * @description
 * @blame wwb
 * @date 2021-03-24 20:12:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignStatDTO {
	
	/** 报名签到id */
	private Integer signId;
	/** 报名人数 */
	private Integer signedUpNum;
	
}
