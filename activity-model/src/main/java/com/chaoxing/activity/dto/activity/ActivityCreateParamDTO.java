package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
	private BigDecimal integral;
	/** 是否开启作品征集 */
	private Boolean openWork;
	/** 来源类型 */
	private String originType;
	/** 来源值 */
	private String origin;
	/** 市场id */
	private Integer marketId;
	/** 模版id */
	private Integer templateId;
	/** 简介 */
	private String introduction;

	/** 活动组件值对象列表 */
	private List<ActivityComponentValueDTO> activityComponentValues;
	/** 操作用户 */
	private LoginUserDTO loginUser;

	/**构建活动对象
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 15:14:51
	 * @param 
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity buildActivity() {
		LocalDateTime startTime = DateUtils.timestamp2Date(getStartTimeStamp());
		LocalDateTime endTime = DateUtils.timestamp2Date(getEndTimeStamp());
		return Activity.builder()
				.name(getName())
				.startTime(startTime)
				.endTime(endTime)
				.startDate(startTime.toLocalDate())
				.endDate(endTime.toLocalDate())
				.coverCloudId(getCoverCloudId())
				.coverUrl("")
				.organisers(getOrganisers())
				.activityType(getActivityType())
				.address(getAddress())
				.detailAddress(getDetailAddress())
				.longitude(getLongitude())
				.dimension(getDimension())
				.activityClassifyId(getActivityClassifyId())
				.period(getPeriod())
				.credit(getCredit())
				.timeLengthUpperLimit(getTimeLengthUpperLimit())
				.timingRelease(getTimingRelease())
				.timingReleaseTime(DateUtils.timestamp2Date(getTimingReleaseTimeStamp()))
				.tags(getTags())
				.openRating(getOpenRating())
				.ratingNeedAudit(getRatingNeedAudit())
				.integral(getIntegral())
				.openWork(getOpenWork())
				.marketId(getMarketId())
				.templateId(getTemplateId())
				.build();
	}

	/**构建活动详情
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 15:21:43
	 * @param 
	 * @return com.chaoxing.activity.model.ActivityDetail
	*/
	public ActivityDetail buildActivityDetail() {
		return ActivityDetail.builder()
				.introduction(getIntroduction())
				.build();
	}

	public static ActivityCreateParamDTO buildDefault() {
		return ActivityCreateParamDTO.builder()
				.name("")
				.coverCloudId(CommonConstant.ACTIVITY_DEFAULT_COVER_CLOUD_ID)
				.organisers("")
				.address("")
				.detailAddress("")
				.openWork(false)
				.build();
	}

	public void buildLoginUser(Integer uid, String userName, Integer fid, String orgName) {
		setLoginUser(LoginUserDTO.buildDefault(uid, userName, fid, orgName));
	}

}