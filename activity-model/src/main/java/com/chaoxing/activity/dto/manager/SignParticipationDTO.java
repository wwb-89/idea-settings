package com.chaoxing.activity.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**参与情况
 * @author wwb
 * @version ver 1.0
 * @className SignParticipationDTO
 * @description
 * @blame wwb
 * @date 2020-11-24 19:27:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignParticipationDTO {

	/** 限制人数 */
	private Integer limitNum;
	/** 报名人数 */
	private Integer signedNum;

}