package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignInDTO
 * @description
 * @blame wwb
 * @date 2021-08-27 10:29:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInDTO {

	/** 主键 */
	private Integer id;
	/** 签到名称 */
	private String name;
	/** 报名签到id */
	private Integer signId;
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
	/** 是否公告签到名单 */
	private Boolean publicList;
	/** 按钮名称 */
	private String btnName;
	/** 签到码是否自动过期 */
	private Boolean codeAutoExpire;
	/** 签到码过期间隔（秒） */
	private Integer codeExpireInterval;
	/** 距离限制（米） */
	private Integer distanceLimit;
	/** 是否重复 */
	private Boolean repeatGenerate;
	/** 重复来源id */
	private Integer repeatOriginId;
	/** 重复周期 */
	private Integer repeatCycle;
	/** 重复周期单位 */
	private String repeatCycleUnit;
	/** 是否自动签退 */
	private Boolean autoSignOut;
	/** 签退延迟小时数 */
	private Integer signOutDelayHour;
	/** 签退延迟分钟数 */
	private Integer signOutDelayMinute;
	/** 签退持续小时数 */
	private Integer signOutDurationHour;
	/** 签退持续分钟数 */
	private Integer signOutDurationMinute;

}