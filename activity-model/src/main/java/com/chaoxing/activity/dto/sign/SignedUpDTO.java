package com.chaoxing.activity.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**报名的报名签到
 * @author wwb
 * @version ver 1.0
 * @className SignedUpDTO
 * @description
 * @blame wwb
 * @date 2021-01-27 18:21:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignedUpDTO {

	/** 报名签到id */
	private Integer signId;
	/** 报名id */
	private Integer signUpId;
	/** 报名状态 */
	private Integer userSignUpStatus;

}