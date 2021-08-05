package com.chaoxing.activity.dto.manager.sign.create;

import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.util.DateUtils;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**创建报名参数对象
 * @author wwb
 * @version ver 1.0
 * @className SignUpCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-12 14:55:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpCreateParamDTO {

	/** 主键 */
	private Integer id;
	/** 报名签到id */
	private Integer signId;
	/** 名称 */
	private String name;
	/** 是否开启审批 */
	private Boolean openAudit;
	/** 开始时间 */
	private Long startTime;
	/** 结束时间 */
	private Long endTime;
	/** 是否限制人数 */
	private Boolean limitPerson;
	/** 人数限制 */
	private Integer personLimit;
	/** 是否填写信息 */
	private Boolean fillInfo;
	/** 填报信息表单类型 */
	private String formType;
	/** 填写信息的表单id */
	private Integer fillInfoFormId;
	/** 是否公开报名名单 */
	private Boolean publicList;
	/** 报名按钮名称 */
	private String btnName;
	/** 是否报名结束后允许取消报名 */
	private Boolean endAllowCancel;
	/** 不允许取消报名的类型 */
	private String notAllowCancelType;
	/** 不允许取消报名的天数; */
	private Integer notAllowCancelDays;
	/** 是否开启微服务参与范围 */
	private Boolean enableWfwParticipateScope;
	/** 是否开启通讯录参与范围 */
	private Boolean enableContactsParticipateScope;
	/** 定制报名类型 */
	private String customSignUpType;
	/** 是否被删除 */
	private Boolean deleted;
	/** 来源id。模版组件id */
	private Integer originId;

	/** 微服务参与范围列表 */
	private List<SignUpParticipateScopeDTO> wfwParticipateScopes;
	/** 通讯录参与范围列表 */
	private List<SignUpParticipateScopeDTO> contactsParticipateScopes;

	public static SignUpCreateParamDTO buildDefault() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = startTime.plusMonths(1);
		return SignUpCreateParamDTO.builder()
				.name("报名")
				.openAudit(false)
				.startTime(DateUtils.date2Timestamp(startTime))
				.endTime(DateUtils.date2Timestamp(endTime))
				.limitPerson(false)
				.personLimit(100)
				.fillInfo(false)
				.publicList(false)
				.btnName("报名参与")
				.endAllowCancel(true)
				.enableWfwParticipateScope(false)
				.enableContactsParticipateScope(false)
				.deleted(false)
				.build();
	}

	@Getter
	public enum CustomSignUpTypeEnum {

		/** 双选会公司报名 */
		DUAL_SELECT_COMPANY("公司报名", "company");

		private String name;
		private String value;

		CustomSignUpTypeEnum(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public static CustomSignUpTypeEnum fromValue(String value) {
			CustomSignUpTypeEnum[] values = CustomSignUpTypeEnum.values();
			for (CustomSignUpTypeEnum customSignUpTypeEnum : values) {
				if (Objects.equals(customSignUpTypeEnum.getValue(), value)) {
					return customSignUpTypeEnum;
				}
			}
			return null;
		}
	}

	@Getter
	public enum NotAllowCancelTypeEnum {

		/** 报名结束后 */
		AFTER_SIGN_UP_END("报名结束后", "after_sign_up_end"),
		AFTER_ACTIVITY_END("活动结束后", "after_activity_end"),
		BEFORE_ACTIVITY_START("活动开始前", "before_activity_start");

		private final String name;
		private final String value;

		NotAllowCancelTypeEnum(String name, String value) {
			this.name = name;
			this.value = value;
		}
		public static NotAllowCancelTypeEnum fromValue(String value) {
			NotAllowCancelTypeEnum[] values = NotAllowCancelTypeEnum.values();
			for (NotAllowCancelTypeEnum notAllowCancelType : values) {
				if (Objects.equals(notAllowCancelType.getValue(), value)) {
					return notAllowCancelType;
				}
			}
			return null;
		}
	}

}