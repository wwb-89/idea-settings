package com.chaoxing.activity.dto.sign;

import com.chaoxing.activity.dto.manager.sign.SignUp;
import com.chaoxing.activity.dto.manager.sign.UserSignUp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityBlockDetailSignStatDTO
 * @description
 * @blame wwb
 * @date 2021-01-14 19:35:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityBlockDetailSignStatDTO {

	/** 报名签到id */
	private Integer signId;
	/** 报名信息 */
	private SignUp signUp;
	/** 报名人数 */
	private Integer signedUpNum;
	/** 用户报名信息 */
	private UserSignUp userSignUp;
	/** 签到数量 */
	private Integer signInNum;

}