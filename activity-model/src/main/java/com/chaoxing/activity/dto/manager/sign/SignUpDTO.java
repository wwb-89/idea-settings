package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignUpDTO
 * @description
 * @blame wwb
 * @date 2021-08-27 10:29:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {

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
	/** 万能表单地址1; column: open_addr*/
	private String openAddr;
	/** 万能表单地址2; column: pc_url*/
	private String pcUrl;
	/** 万能表单地址3; column: wechat_url*/
	private String wechatUrl;
	/** 是否公开报名名单 */
	private Boolean publicList;
	/** 报名按钮名称 */
	private String btnName;
	/** 是否报名结束后允许取消报名 */
	private Boolean endAllowCancel;
	/** 不允许取消报名的类型 */
	private String notAllowCancelType;
	/** 不允许取消报名的天数; */
	private Long notAllowCancelTime;
	/** 是否开启现场报名; */
	private Boolean onSiteSignUp;
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

}