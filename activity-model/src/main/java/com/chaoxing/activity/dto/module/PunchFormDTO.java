package com.chaoxing.activity.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wwb
 * @version ver 1.0
 * @className PunchFormDTO
 * @description
 * @blame wwb
 * @date 2020-11-11 10:15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PunchFormDTO {

	/** column: id*/
	private Integer id;
	/** 名称 */
	private String name;
	/** 介绍 */
	private String introduction;
	/** 封面云盘id，与封面id二选一 */
	private String coverFileCloudId;
	/** 封面id，与封面云盘id二选一 */
	private Integer coverId;
	/** 开始日期 */
	private Date startDate;
	/** 结束日期 */
	private Date endDate;
	/** 开始打卡时间 */
	private Date startTime;
	/** 打卡结束时间 */
	private Date endTime;
	/** 范围限制。0：不限制1：限制 */
	private String scopeLimit;
	/** 需要发表动态。0：否，1：是 */
	private Boolean needPubDynamic;
	/** 需要提交文字。0：否，1：是 */
	private Boolean needSubmitWords;
	/** 需要上传附件。0：否，1：是 */
	private Boolean needUploadAnnex;
	/** 创建人id */
	private Integer createUid;

}