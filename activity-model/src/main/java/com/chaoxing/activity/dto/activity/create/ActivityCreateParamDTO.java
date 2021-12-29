package com.chaoxing.activity.dto.activity.create;

import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.model.ActivityPushReminder;
import com.chaoxing.activity.model.SignUpCondition;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.WfwFormUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
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
	/** 是否开启小组 */
	private Boolean openGroup;
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
	/** 门户页面预览地址 */
	private String previewUrl;
	/** 门户页面修改地址 */
	private String editUrl;
	/** 门户网页模版id */
	private Integer webTemplateId;
	/** 预览显示使用 */
	private String activityClassifyName;
	/** 状态 */
	private Integer status;
	/** 报名成功是否发送通知 */
	private Boolean signedUpNotice;
	/** 活动标识 */
	private String activityFlag;
	/** 源活动id */
	private Integer originActivityId;
	/** 是否开启考核管理 */
	private Boolean openInspectionConfig;
	/** 考核管理id */
	private Integer inspectionConfigId;
	/** 活动组件值对象列表 */
	private List<ActivityComponentValueDTO> activityComponentValues;
	/** 活动报名条件启用模板组件id列表 */
	private List<Integer> sucTemplateComponentIds;
	/** 报名条件配置列表 */
	private List<SignUpCondition> signUpConditions;
	/** 操作用户 */
	private LoginUserDTO loginUser;
	/** 创建人uid */
	private Integer createUid;
	/** 创建机构fid */
	private Integer createFid;
	/** 关联的标签id列表 */
	private List<Integer> tagIds;
	/** 选择的标签名称列表 */
	private List<String> tagNames;
	/** 是否开启班级互动 */
	private Boolean openClazzInteraction;
	/** 班级id */
	private Integer clazzId;
	/** 课程id */
	private Integer courseId;
	/** 证书模版id */
	private Integer certificateTemplateId;
	/** 是否开启推送提醒 */
	private Boolean openPushReminder;

	private ActivityPushReminder activityPushReminder;

	// 附加
	/** 网页模版名称 */
	private String webTemplateName;

	/**构建活动对象
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 15:14:51
	 * @param 
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity buildActivity() {
		LocalDateTime startTime = DateUtils.startTimestamp2Time(getStartTimeStamp());
		LocalDateTime endTime = DateUtils.endTimestamp2Time(getEndTimeStamp());
		defaultValue();
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
				.timingReleaseTime(Optional.ofNullable(getTimingReleaseTimeStamp()).map(v -> DateUtils.startTimestamp2Time(getTimingReleaseTimeStamp())).orElse(null))
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
				.originFormUserId(getOriginFormUserId())
				.originActivityId(getOriginActivityId())
				.openGroup(getOpenGroup())
				.openClazzInteraction(getOpenClazzInteraction())
				.clazzId(getClazzId())
				.courseId(getCourseId())
				.certificateTemplateId(getCertificateTemplateId())
				.openPushReminder(getOpenPushReminder())
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
		ActivityCreateParamDTO activityCreateParam = cloneFromActivity(activity);
		activityCreateParam.setId(activity.getId());
		activityCreateParam.setWorkId(activity.getWorkId());
		activityCreateParam.setReadingId(activity.getReadingId());
		activityCreateParam.setReadingModuleId(activity.getReadingModuleId());
		activityCreateParam.setPreviewUrl(activity.getPreviewUrl());
		activityCreateParam.setEditUrl(activity.getEditUrl());
		activityCreateParam.setOriginActivityId(activity.getOriginActivityId());
		return activityCreateParam;
	}

	/**克隆活动对象
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-07-19 10:42:48
	 * @param activity
	 * @return com.chaoxing.activity.dto.activity.ActivityCreateParamDTO
	 */
	public static ActivityCreateParamDTO cloneFromActivity(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime endTime = activity.getEndTime();
		LocalDateTime timingReleaseTime = activity.getTimingReleaseTime();
		return ActivityCreateParamDTO.builder()
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
				.openReading(activity.getOpenReading())
				.originType(activity.getOriginType())
				.origin(activity.getOrigin())
				.originFormUserId(activity.getOriginFormUserId())
				.webTemplateId(activity.getWebTemplateId())
				.activityClassifyName(activity.getActivityClassifyName())
				.marketId(activity.getMarketId())
				.templateId(activity.getTemplateId())
				.signedUpNotice(activity.getSignedUpNotice())
				.activityFlag(activity.getActivityFlag())
				.openGroup(activity.getOpenGroup())
				.createUid(activity.getCreateUid())
				.createFid(activity.getCreateFid())
				.sucTemplateComponentIds(Lists.newArrayList())
				.signUpConditions(Lists.newArrayList())
				.activityComponentValues(Lists.newArrayList())
				.openClazzInteraction(activity.getOpenClazzInteraction())
				.clazzId(activity.getClazzId())
				.courseId(activity.getCourseId())
				.certificateTemplateId(activity.getCertificateTemplateId())
				.openPushReminder(activity.getOpenPushReminder())
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
		this.openGroup = Optional.ofNullable(openGroup).orElse(false);
		this.openClazzInteraction = Optional.ofNullable(openClazzInteraction).orElse(false);
		this.openPushReminder = Optional.ofNullable(openPushReminder).orElse(false);
	}

	/**从表单数据中获取需要创建的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-05-11 16:14:37
	 * @param formData
	 * @return com.chaoxing.activity.dto.activity.ActivityCreateParamDTO
	 */
	public static ActivityCreateParamDTO buildFromFormData(FormDataDTO formData, Integer classifyId, String orgName) {
		ActivityCreateParamDTO activityCreateParamDto = ActivityCreateParamDTO.buildDefault();
		Integer fid = formData.getFid();
		// 活动名称
		String activityName = WfwFormUtils.getValue(formData, "activity_name");
		activityCreateParamDto.setName(activityName);
		// 封面
		String coverCloudId = WfwFormUtils.getCloudId(formData, "cover");
		if (StringUtils.isNotBlank(coverCloudId)) {
			activityCreateParamDto.setCoverCloudId(coverCloudId);
		}
		// 开始时间、结束时间
		TimeScopeDTO activityTimeScope = WfwFormUtils.getTimeScope(formData, "activity_time");
		activityTimeScope = Optional.ofNullable(activityTimeScope).orElse(WfwFormUtils.getTimeScope(formData, "activity_time_scope"));
		if (activityTimeScope == null) {
			LocalDateTime now = LocalDateTime.now();
			String activityStartTimeStr = WfwFormUtils.getValue(formData, "activity_start_time");
			LocalDateTime startTime = StringUtils.isBlank(activityStartTimeStr) ? now : WfwFormUtils.getTime(activityStartTimeStr);
			activityCreateParamDto.setStartTimeStamp(DateUtils.date2Timestamp(startTime));

			String activityEndTimeStr = WfwFormUtils.getValue(formData, "activity_end_time");
			LocalDateTime endTime = StringUtils.isBlank(activityEndTimeStr) ? startTime.plusMonths(1) : WfwFormUtils.getTime(activityEndTimeStr);
			activityCreateParamDto.setEndTimeStamp(DateUtils.date2Timestamp(endTime));
		} else {
			activityCreateParamDto.setStartTimeStamp(DateUtils.date2Timestamp(activityTimeScope.getStartTime()));
			activityCreateParamDto.setEndTimeStamp(DateUtils.date2Timestamp(activityTimeScope.getEndTime()));
		}
		// 活动分类
		activityCreateParamDto.setActivityClassifyId(classifyId);
		// 积分
		String integralStr = WfwFormUtils.getValue(formData, "integral_value");
		if (StringUtils.isNotBlank(integralStr)) {
			activityCreateParamDto.setIntegral(BigDecimal.valueOf(Double.parseDouble(integralStr)));
		}
		// 主办方
		String organisers = WfwFormUtils.getValue(formData, "organisers");
		organisers = StringUtils.isBlank(organisers) ? orgName : organisers;
		if (StringUtils.isNotBlank(organisers)) {
			activityCreateParamDto.setOrganisers(organisers);
		}
		// 活动类型
		String activityType = WfwFormUtils.getValue(formData, "activity_type");
		Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromName(activityType);

		AddressDTO addressDto = WfwFormUtils.getAddress(formData, "activity_address");
		addressDto = Optional.ofNullable(addressDto).orElse(WfwFormUtils.getAddress(formData, "location"));
		String detailAddress = WfwFormUtils.getValue(formData, "activity_detail_address");
		detailAddress = Optional.ofNullable(detailAddress).orElse("");
		String address = WfwFormUtils.getValue(formData, "activity_address");
		BigDecimal lng = null;
		BigDecimal lat = null;
		if (addressDto != null) {
			address = addressDto.getAddress();
			lng = addressDto.getLng();
			lat = addressDto.getLat();
		}
		if (activityTypeEnum == null) {
			if (StringUtils.isNotBlank(address)) {
				activityTypeEnum = Activity.ActivityTypeEnum.OFFLINE;
			} else {
				activityTypeEnum = Activity.ActivityTypeEnum.ONLINE;
			}
		}
		activityCreateParamDto.setActivityType(activityTypeEnum.getValue());
		activityCreateParamDto.setAddress(address);
		activityCreateParamDto.setDetailAddress(detailAddress);
		activityCreateParamDto.setLongitude(lng);
		activityCreateParamDto.setDimension(lat);

		// 简介
		String introduction = WfwFormUtils.getValue(formData, "introduction");
		introduction = Optional.ofNullable(introduction).orElse("");
		activityCreateParamDto.setIntroduction(introduction);
		// 是否开启评价
		String openRating = WfwFormUtils.getValue(formData, "is_open_rating");
		activityCreateParamDto.setOpenRating(Objects.equals("是", openRating));
		// 是否开启作品征集
		String openWork = WfwFormUtils.getValue(formData, "is_open_work");
		activityCreateParamDto.setOpenWork(Objects.equals("是", openWork));
		// 学分
		String credit = WfwFormUtils.getValue(formData, "credit");
		if (StringUtils.isNotBlank(credit)) {
			activityCreateParamDto.setCredit(new BigDecimal(credit));
		}
		// 学时
		String period = WfwFormUtils.getValue(formData, "period");
		if (StringUtils.isNotBlank(period)) {
			activityCreateParamDto.setPeriod(new BigDecimal(period));
		}
		// 最大参与时长
		String timeLengthUpperLimitStr = WfwFormUtils.getValue(formData, "time_length_upper_limit");
		if (StringUtils.isNotBlank(timeLengthUpperLimitStr)) {
			BigDecimal timeLengthUpperLimit = BigDecimal.valueOf(Double.parseDouble(timeLengthUpperLimitStr));
			activityCreateParamDto.setTimeLengthUpperLimit(timeLengthUpperLimit);
		}
		// 网页模版
		String webTemplateName = WfwFormUtils.getValue(formData, "web_template");
		activityCreateParamDto.setWebTemplateName(webTemplateName);

		activityCreateParamDto.buildLoginUser(formData.getUid(), formData.getUname(), fid, orgName);
		activityCreateParamDto.setOriginType(Activity.OriginTypeEnum.WFW_FORM.getValue());
		activityCreateParamDto.setOrigin(String.valueOf(formData.getFormId()));
		activityCreateParamDto.setOriginFormUserId(formData.getFormUserId());
		return activityCreateParamDto;
	}

	public void setAdditionalAttrs(Integer webTemplateId, Integer marketId, Integer templateId, String flag) {
		this.webTemplateId = webTemplateId;
		this.marketId = marketId;
		this.templateId = templateId;
		this.activityFlag = flag;
	}

}