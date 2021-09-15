package com.chaoxing.activity.dto.activity.create;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**活动创建参数对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreateParamDTO
 * @description 所有通过其它方式创建活动的参数都需要转换成该对象
 * @blame wwb
 * @date 2021-07-13 10:55:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateParamDTO {

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
	private BigDecimal timeLengthUpperLimit;
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
	/** 作品征集id */
	private Integer workId;
	/** 是否开启阅读设置 */
	private Boolean openReading;
	/** 阅读id */
	private Integer readingId;
	/** 阅读模块id */
	private Integer readingModuleId;
	/** 来源类型 */
	private String originType;
	/** 来源值 */
	private String origin;
	/** 来源值记录id */
	private Integer originFormUserId;
	/** 市场id */
	private Integer marketId;
	/** 模版id */
	private Integer templateId;
	/** 简介 */
	private String introduction;

	private String previewUrl;

	private String editUrl;

	private Integer webTemplateId;

	/** 预览显示使用 */
	private String activityClassifyName;

	private Integer status;
	/** 报名成功是否发送通知 */
	private Boolean signedUpNotice;
	/** 活动标识 */
	private String activityFlag;
	/** 源活动id */
	private Integer originActivityId;


	/** 活动组件值对象列表 */
	private List<ActivityComponentValueDTO> activityComponentValues;

	/** 活动报名条件启用模板组件id列表 */
	private List<Integer> sucTemplateComponentIds;
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
				.timingReleaseTime(Optional.ofNullable(getTimingReleaseTimeStamp()).map(v -> DateUtils.timestamp2Date(getTimingReleaseTimeStamp())).orElse(null))
				.tags(getTags())
				.openRating(getOpenRating())
				.ratingNeedAudit(getRatingNeedAudit())
				.integral(getIntegral())
				.openWork(getOpenWork())
				.workId(getWorkId())
				.openReading(getOpenReading())
				.readingId(getReadingId())
				.readingModuleId(getReadingModuleId())
				.marketId(getMarketId())
				.templateId(getTemplateId())
				.status(getStatus())
				.signedUpNotice(getSignedUpNotice())
				.activityFlag(getActivityFlag())
				.originType(getOriginType())
				.origin(getOrigin())
				.originActivityId(getOriginActivityId())
				.build();
	}

	/**构建活动对象
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-07-19 10:42:48
	 * @param activity
	 * @return com.chaoxing.activity.dto.activity.ActivityCreateParamDTO
	 */
	public static ActivityCreateParamDTO buildFromActivity(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime endTime = activity.getEndTime();
		LocalDateTime timingReleaseTime = activity.getTimingReleaseTime();
		return ActivityCreateParamDTO.builder()
				.id(activity.getId())
				.name(activity.getName())
				.startTimeStamp(startTime == null ? null : startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli())
				.endTimeStamp(endTime == null ? null : endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli())
				.coverCloudId(activity.getCoverCloudId())
				.coverUrl(activity.getCoverUrl())
				.organisers(activity.getOrganisers())
				.activityType(activity.getActivityType())
				.address(activity.getAddress())
				.detailAddress(activity.getDetailAddress())
				.longitude(activity.getLongitude())
				.dimension(activity.getDimension())
				.activityClassifyId(activity.getActivityClassifyId())
				.period(activity.getPeriod())
				.credit(activity.getCredit())
				.timeLengthUpperLimit(activity.getTimeLengthUpperLimit())
				.timingRelease(activity.getTimingRelease())
				.timingReleaseTimeStamp(timingReleaseTime == null ? null : timingReleaseTime.toInstant(ZoneOffset.of("+8")).toEpochMilli())
				.createAreaCode(activity.getCreateAreaCode())
				.tags(activity.getTags())
				.openRating(activity.getOpenRating())
				.ratingNeedAudit(activity.getRatingNeedAudit())
				.integral(activity.getIntegral())
				.openWork(activity.getOpenWork())
				.workId(activity.getWorkId())
				.openReading(activity.getOpenReading())
				.readingId(activity.getReadingId())
				.readingModuleId(activity.getReadingModuleId())
				.originType(activity.getOriginType())
				.origin(activity.getOrigin())
				.originFormUserId(activity.getOriginFormUserId())
				.webTemplateId(activity.getWebTemplateId())
				.previewUrl(activity.getPreviewUrl())
				.editUrl(activity.getEditUrl())
				.activityClassifyName(activity.getActivityClassifyName())
				.marketId(activity.getMarketId())
				.templateId(activity.getTemplateId())
				.signedUpNotice(activity.getSignedUpNotice())
				.activityFlag(activity.getActivityFlag())
				.originActivityId(activity.getOriginActivityId())
				.build();
	}

	/**构建活动详情
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 15:21:43
	 * @param activityId
	 * @return com.chaoxing.activity.model.ActivityDetail
	*/
	public ActivityDetail buildActivityDetail(Integer activityId) {
		return ActivityDetail.builder()
				.activityId(activityId)
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

	/**默认值
	 * @Description 没有设值的部分给出默认值
	 * @author wwb
	 * @Date 2021-09-15 15:24:51
	 * @param 
	 * @return void
	*/
	public void defaultValue() {
		this.coverCloudId = Optional.ofNullable(coverCloudId).filter(StringUtils::isNotBlank).orElse(CommonConstant.ACTIVITY_DEFAULT_COVER_CLOUD_ID);
		this.organisers = Optional.ofNullable(organisers).orElse("");
		this.activityType = Optional.ofNullable(activityType).orElse(Activity.ActivityTypeEnum.OFFLINE.getValue());
		this.address = Optional.ofNullable(address).orElse("");
		this.detailAddress = Optional.ofNullable(detailAddress).orElse("");
		this.timingRelease = Optional.ofNullable(timingRelease).orElse(false);
		this.openAudit = Optional.ofNullable(openAudit).orElse(false);
		this.createAreaCode = Optional.ofNullable(createAreaCode).orElse("");
		this.tags = Optional.ofNullable(tags).orElse("");
		this.openRating = Optional.ofNullable(openRating).orElse(false);
		this.ratingNeedAudit = Optional.ofNullable(ratingNeedAudit).orElse(false);
		this.openWork = Optional.ofNullable(openWork).orElse(false);
		this.openReading = Optional.ofNullable(openReading).orElse(false);
	}

}