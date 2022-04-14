package com.chaoxing.activity.dto.manager.sign;

import com.chaoxing.activity.util.constant.DomainConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignDTO
 * @description
 * @blame wwb
 * @date 2021-08-27 10:29:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignDTO {

	/** 主键 */
	private Integer id;
	/** 报名签到名称 */
	private String name;

	/** 报名列表 */
	private List<SignUpDTO> signUps;
	/** 签到列表 */
	private List<SignInDTO> signIns;

	public static String getSignInListUrl(Integer signId) {
		return DomainConstant.SIGN_WEB + "/manage/sign-in/list?signId=" + signId;
	}

	public static String getSignUpManageUrl(Integer signId) {
		return DomainConstant.SIGN_WEB + "/manage/sign-up?signId=" + signId;
	}

	public static String getToSignUpUrl(Integer signId) {
		return DomainConstant.SIGN_WEB + "/sign/" + signId + "/to-sign-up";
	}

}