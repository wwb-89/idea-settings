package com.chaoxing.activity.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**报名签到
 * @author wwb
 * @version ver 1.0
 * @className SignFormDTO
 * @description
 * @blame wwb
 * @date 2020-11-11 10:14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignFormDTO {

	/** column: id*/
	private Integer id;
	/** 活动名称; column: name*/
	private String name;
	/** 说明; column: notes*/
	private String notes;
	/** 封面云盘id; column: cover_cloud_id*/
	private String coverCloudId;
	/** 参与形式; column: partake_form 1-报名 2-签到 3-报名后签到*/
	private String partakeForm;
	/** 报名开始时间; column: sign_up_start_time*/
	private LocalDateTime signUpStartTime;
	/** 报名结束时间; column: sign_up_end_time*/
	private LocalDateTime signUpEndTime;
	/** 签到开始时间; column: sign_in_start_time*/
	private LocalDateTime signInStartTime;
	/** 签到结束时间; column: sign_in_end_time*/
	private LocalDateTime signInEndTime;
	/** 是否限制报名人数; column: is_limit_person*/
	private Boolean limitPerson;
	/** 限制的人数; column: person_limit*/
	private Integer personLimit;
	/** 报名方式; column: sign_up_form*/
	private String signUpForm;
	/** 签到形式; column: sign_in_form 1-普通签到 2-扫码签到 3-位置签到*/
	private String signInForm;
	/** 地址; column: address*/
	private String address;
	/** 经度; column: longitude*/
	private BigDecimal longitude;
	/** 维度; column: dimension*/
	private BigDecimal dimension;
	/** 是否开启报名信息填写; column: is_sign_up_info_write*/
	private Boolean signUpInfoWrite;
	/** 报名信息填写的表单id; column: sign_up_form_id*/
	private String signUpFormId;
	/** 是否开启签到信息填写; column: is_sign_in_info_write*/
	private Boolean signInInfoWrite;
	/** 签到信息填写的表单id; column: sign_in_form_id*/
	private String signInFormId;
	/** 是否公开报名名单; column: is_public_sign_up_list*/
	private Boolean publicSignUpList;
	/** 是否公开签到名单; column: is_public_sign_in_list*/
	private Boolean publicSignInList;
	/** 报名按钮名称; column: sign_up_btn_name*/
	private String signUpBtnName;
	/** 签到按钮名称; column: sign_in_btn_name*/
	private String signInBtnName;
	/** 创建人uid; column: create_uid*/
	private Integer createUid;
	/** 创建人姓名; column: create_user_name*/
	private String createUserName;
	/** 创建人fid; column: create_fid*/
	private Integer createFid;
	/** 创建人机构名称; column: create_org_name*/
	private String createOrgName;

	// 附加
	/** 报名开始时间字符串表示 */
	private String signUpStartTimeStr;
	/** 报名结束时间字符串表示 */
	private String signUpEndTimeStr;
	/** 签到开始时间字符串表示 */
	private String signInStartTimeStr;
	/** 签到结束时间字符串表示 */
	private String signInEndTimeStr;

}