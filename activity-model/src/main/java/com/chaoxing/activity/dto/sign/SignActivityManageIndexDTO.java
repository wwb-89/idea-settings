package com.chaoxing.activity.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动管理首页需要的报名签到信息
 * @author wwb
 * @version ver 1.0
 * @className SignActivityManageIndexDTO
 * @description
 * @blame wwb
 * @date 2020-12-24 10:42:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignActivityManageIndexDTO {

	/** 报名签到id */
	private Integer signId;
	/** 报名存在 */
	private Boolean signUpExist;
	/** 报名id */
	private Integer signUpId;
	/** 签到存在 */
	private Boolean signInExist;
	/** 报名人数 */
	private Integer signUpNum;

}