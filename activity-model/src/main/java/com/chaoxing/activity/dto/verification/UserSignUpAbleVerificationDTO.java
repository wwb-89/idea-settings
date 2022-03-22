package com.chaoxing.activity.dto.verification;

import lombok.Data;

/**用户能都报名验证对象
 * @author wwb
 * @version ver 1.0
 * @className UserSignUpAbleVerificationDTO
 * @description
 * @blame wwb
 * @date 2022-03-22 11:38:10
 */
@Data
public class UserSignUpAbleVerificationDTO {

	/** 验证通过 */
	private Boolean verified;
	/** 验证不通过的信息 */
	private String message;
	/** 验证不通过点击信息跳转的链接 */
	private String messageClickJumpUrl;

	/** 在黑名单中 */
	private Boolean inBlacklist;
	/** 解封时间 */
	private Long unlockTime;

	public static UserSignUpAbleVerificationDTO buildDefault() {
		UserSignUpAbleVerificationDTO userSignUpAbleVerification = new UserSignUpAbleVerificationDTO();
		userSignUpAbleVerification.verified();
		return userSignUpAbleVerification;
	}

	public void verified() {
		setVerified(true);
		setMessage("");
		setMessageClickJumpUrl("");
		setInBlacklist(false);
		setUnlockTime(null);
	}

	public void verifyFail() {
		verifyFail("验证失败");
	}

	public void verifyFail(String message) {
		verifyFail(message, "");
	}

	public void verifyFail(String message, String messageClickJumpUrl) {
		setVerified(false);
		setMessage(message);
		setMessageClickJumpUrl(messageClickJumpUrl);
		setInBlacklist(false);
		setUnlockTime(null);
	}

	public void inBlacklist(Long unlockTime) {
		verifyFail();
		setInBlacklist(true);
		setUnlockTime(unlockTime);
	}

}