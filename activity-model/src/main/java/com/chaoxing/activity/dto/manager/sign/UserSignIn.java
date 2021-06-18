package com.chaoxing.activity.dto.manager.sign;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserSignIn
 * @description
 * @blame wwb
 * @date 2021-06-17 16:17:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignIn {

	/** 主键 */
	private Integer id;
	/** 用户id */
	private Integer uid;
	/** 用户名 */
	private String uname;
	/** 用户姓名 */
	private String userName;
	/** 报名签到id */
	private Integer signId;
	/** 签到id */
	private Integer signInId;
	/** 签到日期 */
	private LocalDate signInDate;
	/** 签到时间 */
	private LocalDateTime signInTime;
	/** 签到状态。0：请假，1：已签，2：未签 */
	private Integer signInStatus;
	/** 参与时长（分钟） */
	private Integer participateInLength;
	/** 是否代理签到 */
	private Boolean proxy;
	/** 代理人id */
	private Integer proxyUid;
	/** 代理人姓名 */
	private String proxyUserName;
	/** 创建时间 */
	private LocalDateTime createTime;
	/** 创建人id */
	private Integer createUid;
	/** 创建人姓名 */
	private String createUserName;
	/** 创建人fid */
	private Integer createFid;
	/** 创建人机构名 */
	private String createOrgName;
	/** 更新时间 */
	private LocalDateTime updateTime;
	/** 更新人id */
	private Integer updateUid;

	/** 签到名称 */
	private String signInName;

	@Getter
	public enum SignInStatusEnum {

		/** 请假 */
		LEAVE("请假", 0),
		NOT_SIGNED_IN("未签", 2),
		SIGNED_IN("已签", 1);

		private final String name;
		private final Integer value;

		SignInStatusEnum(String name, Integer value) {
			this.name = name;
			this.value = value;
		}

		public static UserSignIn.SignInStatusEnum fromValue(Integer value) {
			UserSignIn.SignInStatusEnum[] values = UserSignIn.SignInStatusEnum.values();
			for (UserSignIn.SignInStatusEnum signInStatusEnum : values) {
				if (Objects.equals(signInStatusEnum.getValue(), value)) {
					return signInStatusEnum;
				}
			}
			return null;
		}

		public static UserSignIn.SignInStatusEnum fromName(String name) {
			UserSignIn.SignInStatusEnum[] values = UserSignIn.SignInStatusEnum.values();
			for (UserSignIn.SignInStatusEnum signInStatusEnum : values) {
				if (Objects.equals(signInStatusEnum.getName(), name)) {
					return signInStatusEnum;
				}
			}
			return null;
		}

	}

}
