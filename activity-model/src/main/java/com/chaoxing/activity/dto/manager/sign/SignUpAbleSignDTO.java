package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**能报名签到的报名签到对象
 * @author wwb
 * @version ver 1.0
 * @className SignUpAbleSignDTO
 * @description
 * @blame wwb
 * @date 2021-10-18 10:18:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAbleSignDTO {

	/** 报名签到id */
	private Integer signId;
	/** 用户id */
	private Integer uid;
	/** 报名状态 */
	private Integer signUpStatus;
	/** 报名状态描述 */
	private String signUpStatusDescribe;

}