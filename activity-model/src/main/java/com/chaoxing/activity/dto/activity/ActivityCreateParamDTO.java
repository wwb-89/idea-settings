package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**创建活动对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-13 10:55:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateParamDTO {

	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 开始时间 */
	private Long startTimeStamp;
	/** 结束时间 */
	private Long endTimeStamp;
	/** 封面云盘id */
	private String coverCloudId;
	/** 封面地址 */
	private String coverUrl;
	/** 主办方 */
	private String organisers;
	/** 活动形式 */
	private String activityType;
	/** 活动地址 */
	private String address;
	/** 详细地址 */
	private String detailAddress;
	/** 经度 */
	private BigDecimal longitude;
	/** 维度 */
	private BigDecimal dimension;
	/** 活动分类id */
	private Integer activityClassifyId;
	/** 学时 */
	private BigDecimal period;
	/** 学分 */
	private BigDecimal credit;
	/** 参与时长上限（小时） */
	private Integer timeLengthUpperLimit;
	/** 是否启用签到报名 */
	private Boolean enableSign;
	/** 签到报名id */
	private Integer signId;
	/** 网页模板id */
	private Integer webTemplateId;
	/** 门户网站id */
	private Integer websiteId;
	/** 门户网页id */
	private Integer pageId;
	/** 门户预览地址 */
	private String previewUrl;
	/** 门户编辑地址 */
	private String editUrl;
	/** 是否定时发布 */
	private Boolean timingRelease;
	/** 定时发布时间 */
	private Long timingReleaseTimeStamp;
	/** 是否开启审核 */
	private Boolean openAudit;
	/** 创建区域编码 */
	private String createAreaCode;
	/** 标签。以逗号分隔 */
	private String tags;
	/** 是否开启评价 */
	private Boolean openRating;
	/** 评价是否需要审核 */
	private Boolean ratingNeedAudit;
	/** 积分值 */
	private BigDecimal integralValue;
	/** 是否开启作品征集 */
	private Boolean openWork;
	/** 作品征集id */
	private Integer workId;
	/** 来源类型 */
	private String originType;
	/** 来源值 */
	private String origin;
	/** 市场id */
	private Integer marketId;
	/** 模版id */
	private Integer templateId;

//	public Activity buildActivity() {
//		return Activity.builder()
//				.id()
//				.name()
//				.startTime()
//				.endTime()
//				.startDate()
//				.endDate()
//				.coverCloudId()
//				.coverUrl()
//				.organisers()
//				.activityType()
//				.address()
//				.detailAddress()
//				.longitude()
//				.dimension()
//				.activityClassifyId()
//				.period()
//				.credit()
//				.timeLengthUpperLimit()
//				.signId()
//				.webTemplateId()
//				.websiteId()
//				.pageId()
//				.previewUrl()
//				.editUrl()
//				.timingRelease()
//				.timingReleaseTime()
//				.tags()
//				.openRating()
//				.ratingNeedAudit()
//				.integralValue()
//				.openWork()
//				.workId()
//				.marketId()
//				.templateId()
//				.build();
//	}

}