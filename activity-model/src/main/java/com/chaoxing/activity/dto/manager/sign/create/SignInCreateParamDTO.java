package com.chaoxing.activity.dto.manager.sign.create;

import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**创建签到参数对象
 * @author wwb
 * @version ver 1.0
 * @className SignInCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-12 14:55:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInCreateParamDTO {

	/** 主键 */
	private Integer id;
	/** 报名签到id */
	private Integer signId;
	/** 签到名称 */
	private String name;
	/** 签到类型：签到、签退 */
	private String type;
	/** 签退关联的签到id， 当类型是签退时有效 */
	private Integer signInId;
	/** 签到码 */
	private String code;
	/** 开始时间 */
	private Long startTime;
	/** 结束时间 */
	private Long endTime;
	/** 签到方式。1：普通签到、2：位置签到、3：二维码签到 */
	private Integer way;
	/** 签到地址 */
	private String address;
	/** 详细地址 */
	private String detailAddress;
	/** 签到经度 */
	private BigDecimal longitude;
	/** 签到维度 */
	private BigDecimal dimension;
	/** 扫码方式。1：参与者扫码，2：管理员扫码 */
	private Integer scanCodeWay;
	/** 是否填写信息 */
	private Boolean fillInfo;
	/** 填写信息的表单id */
	private Integer fillInfoFormId;
	/** 是否公告签到名单 */
	private Boolean publicList;
	/** 是否被删除 */
	private Boolean deleted;
	/** 来源id。模版组件id */
	private Integer originId;
	/** 按钮名称 */
	private String btnName;

	public static SignInCreateParamDTO buildDefaultSignIn() {
		SignInCreateParamDTO signIn = buildDefault();
		signIn.setName("签到");
		signIn.setType("sign_in");
		return signIn;
	}

	public static SignInCreateParamDTO buildDefaultSignOut() {
		SignInCreateParamDTO signIn = buildDefault();
		signIn.setName("签退");
		signIn.setType("sign_out");
		return signIn;
	}

	private static SignInCreateParamDTO buildDefault() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = startTime.plusMonths(1);
		return SignInCreateParamDTO.builder()
				.name("签到")
				.startTime(DateUtils.date2Timestamp(startTime))
				.endTime(DateUtils.date2Timestamp(endTime))
				.way(1)
				.address("")
				.detailAddress("")
				.btnName("签到")
				.scanCodeWay(1)
				.fillInfo(false)
				.publicList(false)
				.deleted(false)
				.build();
	}

	@Getter
	public enum Way {

		/** 普通签到 */
		DIRECT("普通签到", 1),
		POSITION("位置签到", 2),
		QR_CODE("二维码签到", 3);

		private String name;
		private Integer value;

		Way(String name, Integer value) {
			this.name = name;
			this.value = value;
		}

		public static Way fromValue(Integer value) {
			Way[] values = Way.values();
			for (Way way : values) {
				if (Objects.equals(way.getValue(), value)) {
					return way;
				}
			}
			return null;
		}

		public static Way fromName(String name) {
			Way[] values = Way.values();
			for (Way way : values) {
				if (Objects.equals(way.getName(), name)) {
					return way;
				}
			}
			return null;
		}

		public static void notNull(Way way) {
			Optional.ofNullable(way).orElseThrow(() -> new BusinessException("未知的签到方式"));
		}

	}

	@Getter
	public enum ScanCodeWay {

		/** 参与者扫码 */
		PARTICIPATOR("参与者扫码", 1),
		MANAGER("管理员扫码", 2);

		private String name;
		private Integer value;

		ScanCodeWay(String name, Integer value) {
			this.name = name;
			this.value = value;
		}

		public static ScanCodeWay fromValue(Integer value) {
			ScanCodeWay[] values = ScanCodeWay.values();
			for (ScanCodeWay way : values) {
				if (Objects.equals(way.getValue(), value)) {
					return way;
				}
			}
			return null;
		}

		public static ScanCodeWay fromName(String name) {
			ScanCodeWay[] values = ScanCodeWay.values();
			for (ScanCodeWay way : values) {
				if (Objects.equals(way.getName(), name)) {
					return way;
				}
			}
			return null;
		}

	}

}