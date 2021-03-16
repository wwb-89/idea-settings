package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户报名签到参与情况统计信息
 * @author wwb
 * @version ver 1.0
 * @className UserSignParticipationStatDTO
 * @description
 * @blame wwb
 * @date 2021-03-09 17:12:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignParticipationStatDTO {

	/** 报名签到id */
	private Integer signId;
	/** 报名id */
	private Integer signUpId;
	/** 报名未开始 */
	private Boolean signUpNotStart;
	/** 报名已结束 */
	private Boolean signUpEnded;
	/** 没有名额 */
	private Boolean noPlaces;
	/** 是否在参与范围 */
	private Boolean inParticipationScope;
	/** 签到id列表 */
	private List<Integer> signInIds;
	/** 用户id */
	private Integer uid;
	/** 是否报名成功 */
	private Boolean signedUp;
	/** 是否报名审核中 */
	private Boolean signUpAudit;

	/** 报名地址 */
	private String signUpUrl;
	/** 签到地址 */
	private String signInUrl;
	/** 报名信息地址 */
	private String signUpResultUrl;

}