package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.*;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.query.ActivityReleasePlatformActivityQueryDTO;
import com.chaoxing.activity.dto.activity.query.result.ActivityReleasePlatformActivityQueryResultDTO;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpAbleSignDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignUpStatusStatDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityCreateParticipateQueryDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.mapper.ActivityDetailMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.mapper.ActivityRatingDetailMapper;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.component.ComponentQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.activity.engine.CustomAppConfigQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityPushReminderService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.service.tag.TagQueryService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.*;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.chaoxing.activity.util.enums.ActivityQueryDateScopeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**活动查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityQueryService
 * @description
 * @blame wwb
 * @date 2020-11-10 15:51:03
 */
@Slf4j
@Service
public class ActivityQueryService {

	@Resource
	private ActivityMapper activityMapper;
	@Resource
	private ActivityDetailMapper activityDetailMapper;
	@Resource
	private ActivityRatingDetailMapper activityRatingDetailMapper;

	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityManagerQueryService activityManagerQueryService;
	@Resource
	private ClassifyQueryService classifyQueryService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private TemplateComponentService templateComponentQueryService;
	@Resource
	private TableFieldDetailMapper tableFieldDetailMapper;

	@Resource
	private ActivityComponentValueService activityComponentValueService;
	@Resource
	private SignUpConditionService signUpConditionService;
	@Resource
	private ComponentQueryService componentQueryService;
	@Resource
	private MarketQueryService marketQueryService;
	@Resource
	private ActivityStatSummaryQueryService activityStatSummaryQueryService;
	@Resource
	private TableFieldQueryService tableFieldQueryService;
	@Resource
	private CloudApiService cloudApiService;
	@Resource
	private PassportApiService passportApiService;
	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;
	@Resource
	private ActivityMenuService activityMenuService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private TagQueryService tagQueryService;
	@Resource
	private ActivityScopeQueryService activityScopeQueryService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityPushReminderService activityPushReminderService;
	@Resource
	private CustomAppConfigQueryService customAppConfigQueryService;

	/**查询参与的活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 14:21:27
	 * @param page
	 * @param activityQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> listParticipate(Page<Activity> page, ActivityQueryDTO activityQuery) {
		calDateScope(activityQuery);
		Integer currentUid = activityQuery.getCurrentUid();
		Boolean signUpAble = activityQuery.getSignUpAble();
		List<String> tagNames = activityQuery.getTags();
		List<Tag> tags = tagQueryService.listByNames(tagNames);
		List<Integer> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(tagNames) && CollectionUtils.isEmpty(tagIds)) {
			// 填充一个不存在的tag
			tagIds.add(-1);
		}
		activityQuery.setTagIds(tagIds);
		if (currentUid != null && Optional.ofNullable(signUpAble).orElse(false)) {
			page = pageAllSignUpAbleActivity(page, activityQuery, currentUid);
		} else {
			page = activityMapper.pageParticipate(page, activityQuery);
			packageActivitySignUpStatus(currentUid, page.getRecords());
		}
		packageActivitySignedStat(page);
		return page;
	}

	/**查询所有用户可报名的活动，并将活动封装到page返回
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-13 16:34:28
	 * @param page
	 * @param activityQuery
	 * @param currentUid
	 * @return
	 */
	private Page<Activity> pageAllSignUpAbleActivity(Page<Activity> page, ActivityQueryDTO activityQuery, Integer currentUid) {
		page.setSize(Integer.MAX_VALUE);
		// 查询可报名活动时，过滤掉已经结束的状态的活动
		if (CollectionUtils.isNotEmpty(activityQuery.getStatusList())) {
			activityQuery.getStatusList().remove(Activity.StatusEnum.ENDED.getValue());
		}
		page = activityMapper.pageParticipate(page, activityQuery);
		List<Activity> records = Optional.ofNullable(page.getRecords()).orElse(Lists.newArrayList());
		packageActivitySignUpStatus(currentUid, records);
		List<Activity> activities = records.stream().filter(v -> null != v.getHasSignUp() && v.getHasSignUp()).collect(Collectors.toList());
		page.setRecords(activities);
		page.setTotal(activities.size());
		return page;
	}

	/**封装活动的报名状态信息
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-13 17:07:49
	 * @param currentUid
	 * @param records
	 * @return
	 */
	private void packageActivitySignUpStatus(Integer currentUid, List<Activity> records) {
		if (currentUid == null || CollectionUtils.isEmpty(records)) {
			return;
		}
		// 只查询能报名的
		List<Integer> signIds = records.stream().map(Activity::getSignId).filter(Objects::nonNull).collect(Collectors.toList());
		List<SignUpAbleSignDTO> signUpAbleSigns = signApiService.listSignUpAbleSign(currentUid, signIds);
		if (CollectionUtils.isNotEmpty(signUpAbleSigns)) {
			Map<Integer, SignUpAbleSignDTO> signIdSignUpAbleSignMap = signUpAbleSigns.stream().collect(Collectors.toMap(SignUpAbleSignDTO::getSignId, v -> v, (v1, v2) -> v2));
			for (Activity record : records) {
				SignUpAbleSignDTO signUpAbleSign = signIdSignUpAbleSignMap.get(record.getSignId());
				if (signUpAbleSign != null) {
					record.setHasSignUp(true);
					record.setSignUpStatus(signUpAbleSign.getSignUpStatus());
					record.setSignUpStatusDescribe(signUpAbleSign.getSignUpStatusDescribe());
				}
			}
		}
	}

	/**查询flag下的所有活动
	 * @Description
	 * @author wwb
	 * @Date 2021-10-11 17:57:37
	 * @param page
	 * @param activityQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> pageFlag(Page<Activity> page, ActivityQueryDTO activityQuery) {
		calDateScope(activityQuery);
		Integer currentUid = activityQuery.getCurrentUid();
		Boolean signUpAble = Optional.ofNullable(activityQuery.getSignUpAble()).orElse(false);
		if (currentUid != null && signUpAble) {
			page = pageAllSignUpAbleActivity(page, activityQuery, currentUid);
		} else {
			page = activityMapper.pageFlag(page, activityQuery);
			packageActivitySignUpStatus(currentUid, page.getRecords());
		}
		packageActivitySignedStat(page);
		return page;
	}

	/**鄂尔多斯可参与活动查询
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-03 15:44:46
	 * @param page
	 * @param activityQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> pageErdosParticipate(Page<Activity> page, ActivityQueryDTO activityQuery) {
		List<Integer> fids = new ArrayList<>();
		if (Objects.equals(activityQuery.getLevelType(), "class") && activityQuery.getUserClassId() == null) {
			return page;
		}
		Integer topFid = activityQuery.getTopFid();
		if (StringUtils.isNotBlank(activityQuery.getFlag())) {
			activityQuery.setFlags(Arrays.asList(activityQuery.getFlag().split(",")));
		}
		List<WfwAreaDTO> wfwRegionalArchitectures = wfwAreaApiService.listByFid(topFid);
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(topFid);
		}
		activityQuery.setFids(fids);
		return activityMapper.pageErdosParticipate(page, activityQuery);
	}

	public Page<Activity> erdosMhDatacenterPage(Page<Activity> page, ActivityQueryDTO activityQuery) {
		return activityMapper.erdosMhDatacenterPage(page, activityQuery);
	}

	/**活动日历查询
	 * @Description
	 * @author wwb
	 * @Date 2020-12-03 16:07:47
	 * @param page
	 * @param mhActivityCalendarQuery
	 * @param multi 活动的时间范围内是否需要返回多条数据
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> listActivityCalendar(Page<Activity> page, MhActivityCalendarQueryDTO mhActivityCalendarQuery, boolean multi) throws ParseException {
		Integer strict = mhActivityCalendarQuery.getStrict();
		if (Objects.equals(1, strict)) {
			page = activityMapper.pageActivityCalendarCreated(page, mhActivityCalendarQuery);
		} else {
			page = activityMapper.pageActivityCalendarParticipate(page, mhActivityCalendarQuery);
		}
		List<Activity> records = page.getRecords();
		String startDateStr = mhActivityCalendarQuery.getStartDate();
		String endDateStr = mhActivityCalendarQuery.getEndDate();
		if (CollectionUtils.isNotEmpty(records) && StringUtils.isNotBlank(startDateStr)) {
			LocalDateTime startDateTime = DateUtils.timestamp2Date(DateFormatConstant.YYYYMMDD.parse(startDateStr).getTime());
			LocalDateTime endDateTime = DateUtils.timestamp2Date(DateFormatConstant.YYYYMMDD.parse(endDateStr).getTime());
			// 每个活动的开始时间到结束时间
			List<Activity> activities = Lists.newArrayList();
			page.setRecords(activities);
			if (multi) {
				for (Activity record : records) {
					LocalDateTime startTime = record.getStartTime();
					LocalDateTime endTime = record.getEndTime();
					if (startTime.isBefore(startDateTime)) {
						startTime = startDateTime;
					}
					if (endTime.isAfter(endDateTime)) {
						endTime = endDateTime;
					}
					Calendar startCalendar = Calendar.getInstance();
					startCalendar.set(startTime.getYear(), startTime.getMonthValue() - 1, startTime.getDayOfMonth(), 0, 0, 0);
					while (startTime.isBefore(endTime)) {
						Activity activity = new Activity();
						BeanUtils.copyProperties(record, activity);
						activity.setStartTime(DateUtils.timestamp2Date(startCalendar.getTime().getTime()));
						activities.add(activity);
						startCalendar.add(Calendar.DAY_OF_MONTH, 1);
						startTime = DateUtils.timestamp2Date(startCalendar.getTime().getTime());
					}
				}
			}
		}
		return page;
	}

	/**查询机构创建的或能参与的
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 21:48:23
	 * @param page
	 * @param fid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> listOrgParticipatedOrCreated(Page<Activity> page, Integer fid) {
		page = activityMapper.listOrgParticipatedOrCreated(page, fid);
		return page;
	}

	/**查询市场创建的（参与）
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-22 18:12:47
	 * @param page
	 * @param marketId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	public Page<Activity> listMarketCreated(Page<Activity> page, Integer marketId) {
		page = activityMapper.listMarketCreated(page, marketId);
		return page;
	}

	/**计算查询的时间范围
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 14:21:57
	 * @param activityQuery
	 * @return void
	 */
	private void calDateScope(ActivityQueryDTO activityQuery) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dateScope = Optional.ofNullable(activityQuery.getDateScope()).orElse("");
		ActivityQueryDateScopeEnum activityQueryDateEnum = ActivityQueryDateScopeEnum.fromValue(dateScope);
		LocalDate today = LocalDate.now();
		String minTimeStr;
		String maxTimeStr;
		switch (activityQueryDateEnum) {
			case ALL:
				minTimeStr = "";
				maxTimeStr = "";
				break;
			case NEARLY_A_MONTH:
				minTimeStr = today.atTime(0, 0, 0).plusMonths(-1).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case NEARLY_THREE_MONTH:
				minTimeStr = today.atTime(0, 0, 0).plusMonths(-3).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case NEARLY_SIX_MONTH:
				minTimeStr = today.atTime(0, 0, 0).plusMonths(-6).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case NEARLY_A_YEAR:
				minTimeStr = today.atTime(0, 0, 0).plusYears(-1).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case TODAY:
				minTimeStr = today.atTime(0, 0, 0).format(dateTimeFormatter);
				maxTimeStr = today.atTime(23, 59, 59).format(dateTimeFormatter);
				break;
			case TOMORROW:
				minTimeStr = today.atTime(0, 0, 0).plusDays(1).format(dateTimeFormatter);
				maxTimeStr = today.atTime(23, 59, 59).plusDays(1).format(dateTimeFormatter);
				break;
			case WEEKEND:
				// 周末
				minTimeStr = today.with(DayOfWeek.SATURDAY).atTime(0, 0, 0).format(dateTimeFormatter);
				maxTimeStr = today.with(DayOfWeek.SUNDAY).atTime(23, 59, 59).format(dateTimeFormatter);
				break;
			case NEARLY_A_WEEK:
				minTimeStr = today.atTime(0,0,0).format(dateTimeFormatter);
				maxTimeStr = today.atTime(23,59,59).plusWeeks(1).format(dateTimeFormatter);
				break;
			case SPECIFIED:
				String date = activityQuery.getDate();
				if (StringUtils.isNotBlank(date)) {
					LocalDate specifiedDate = LocalDate.parse(date, dateFormatter);
					minTimeStr = specifiedDate.atTime(0, 0, 0).format(dateTimeFormatter);
					maxTimeStr = specifiedDate.atTime(23, 59, 59).format(dateTimeFormatter);
				} else {
					minTimeStr = "";
					maxTimeStr = "";
				}
				break;
			default:
				// 更早
				minTimeStr = "";
				maxTimeStr = today.atTime(0, 0, 0).plusYears(-1).format(dateTimeFormatter);
		}
		activityQuery.setMinTimeStr(minTimeStr);
		activityQuery.setMaxTimeStr(maxTimeStr);
	}

	/**查询活动类型列表
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 17:50:59
	 * @param
	 * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityTypeDTO>
	 */
	public List<ActivityTypeDTO> listActivityType() {
		return Arrays.stream(Activity.ActivityTypeEnum.values()).map(ActivityTypeDTO::buildFromActivityTypeEnum).collect(Collectors.toList());
	}

	/**查询管理的活动列表
	 * @Description
	 * @author wwb
	 * @Date 2020-11-18 14:31:38
	 * @param page
	 * @param activityManageQuery
	 * @param loginUser
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> listManaging(Page<Activity> page, ActivityManageQueryDTO activityManageQuery, LoginUserDTO loginUser) {
		Integer strict = Optional.ofNullable(activityManageQuery.getStrict()).orElse(0);
		activityManageQuery.setOrderField(Optional.ofNullable(activityManageQuery.getOrderFieldId()).map(tableFieldDetailMapper::selectById).map(TableFieldDetail::getCode).orElse(""));
		activityManageQuery.setFids(new ArrayList(){{add(activityManageQuery.getFid());}});
		if (strict.compareTo(1) == 0) {
			// 严格模式
			activityManageQuery.setCreateUid(loginUser.getUid());
			activityManageQuery.setCreateWfwfid(activityManageQuery.getFid());
			page = activityMapper.pageCreated(page, activityManageQuery);
		} else {
			if (StringUtils.isNotBlank(activityManageQuery.getCode())) {
				List<WfwAreaDTO> wfwAreas = wfwAreaApiService.listByCode(activityManageQuery.getCode());
				WfwAreaDTO currWfwArea = wfwAreas.stream().filter(v -> Objects.equals(activityManageQuery.getFid(), v.getFid())).findFirst().orElse(null);
				if (currWfwArea != null) {
					List<Integer> subFids = wfwAreas.stream().filter(v -> StringUtils.startsWith(v.getCode(), currWfwArea.getCode())).map(WfwAreaDTO::getFid).collect(Collectors.toList());
					activityManageQuery.setFids(subFids);
				}
				// 如果区域flag、areaCode不为空 则清空marketId
				if (StringUtils.isNotBlank(activityManageQuery.getActivityFlag())) {
					activityManageQuery.setMarketId(null);
				}
			}
			page = activityMapper.pageManaging(page, activityManageQuery);
		}
		List<Activity> activities = page.getRecords();
		// 封装活动关联数据
		packageActivityData(activities);
		// 封装是不是管理员
		packageManager(activities);
		return page;
	}

	/**封装活动关联数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-18 15:17:20
	 * @param activities
	 * @return void
	 */
	private void packageActivityData(List<Activity> activities) {
		if (CollectionUtils.isEmpty(activities)) {
			return;
		}
		List<Integer> signIds = activities.stream().map(Activity::getSignId).filter(Objects::nonNull).collect(Collectors.toList());
		List<Integer> activityIds = activities.stream().map(Activity::getId).collect(Collectors.toList());
		// 根据活动id查询活动统计汇总列表
		Map<Integer, ActivityStatSummaryDTO> statSummaryMap = activityStatSummaryQueryService.listActivitySummariesByIds(activityIds)
				.stream()
				.collect(Collectors.toMap(ActivityStatSummaryDTO::getActivityId, v -> v, (v1, v2) -> v2));
		Map<Integer, SignStatDTO> signIdSignStatMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(signIds)) {
			List<SignStatDTO> signStats = signApiService.statSignSignUps(signIds);
			signIdSignStatMap = signStats.stream().collect(Collectors.toMap(SignStatDTO::getId, v -> v, (v1, v2) -> v2));
		}
		// 自定义组件查询
		List<ActivityComponentValue> activityComponentValues = activityComponentValueService.listByActivityIds(activityIds);
		Map<Integer, List<ActivityComponentValue>> activityComponentValueMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(activityComponentValues)) {
			activityComponentValues.forEach(v -> {
				Integer activityId = v.getActivityId();
				activityComponentValueMap.computeIfAbsent(activityId, k -> Lists.newArrayList());
				activityComponentValueMap.get(activityId).add(v);
			});
		}
		// 活动标签
		List<ActivityTagNameDTO> activityTagNames = tagQueryService.listActivityTagNameByActivityIds(activityIds);
		Map<Integer, List<String>> activityIdTagNames = activityTagNames.stream().collect(Collectors.groupingBy(ActivityTagNameDTO::getActivityId, Collectors.mapping(ActivityTagNameDTO::getTagName, Collectors.toList())));
		for (Activity activity : activities) {
			// 活动报名签到状态数据
			SignStatDTO signStatItem = Optional.ofNullable(activity.getSignId()).map(signIdSignStatMap::get).orElse(null);
			activity.setSignedUpNum(Optional.ofNullable(signStatItem).map(SignStatDTO::getSignedUpNum).orElse(0));
			activity.setPersonLimit(Optional.ofNullable(signStatItem).map(SignStatDTO::getLimitNum).orElse(0));
			// 活动统计数据
			ActivityStatSummaryDTO summaryItem = statSummaryMap.get(activity.getId());
			activity.setSignedInNum(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getSignedInNum).orElse(0));
			activity.setSignedInRate(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getSignInRate).orElse(new BigDecimal(0)));
			activity.setRateNum(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getRateNum).orElse(0));
			activity.setRateScore(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getRateScore).orElse(new BigDecimal(0)));
			activity.setQualifiedNum(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getQualifiedNum).orElse(0));
			activity.setActivityComponentValues(Optional.ofNullable(activityComponentValueMap.get(activity.getId())).orElse(Lists.newArrayList()));
			// 标签
			List<String> tagNames = activityIdTagNames.getOrDefault(activity.getId(), Lists.newArrayList());
			activity.setTags(String.join(CommonConstant.LINK_CHAR, tagNames));

		}
	}
	/** 查询并设置活动已报名人数
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-27 15:12:25
	 * @param
	 * @return void
	 */
	private void packageActivitySignedStat(Page<Activity> page) {
		if (CollectionUtils.isEmpty(page.getRecords())) {
			return;
		}
		List<Integer> signIds = page.getRecords().stream().map(Activity::getSignId).filter(Objects::nonNull).collect(Collectors.toList());
		Map<Integer, SignStatDTO> signIdSignStatMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(signIds)) {
			List<SignStatDTO> signStats = signApiService.statSignSignUps(signIds);
			signIdSignStatMap = signStats.stream().collect(Collectors.toMap(SignStatDTO::getId, v -> v, (v1, v2) -> v2));
		}
		for (Activity activity : page.getRecords()) {
			// 活动报名签到状态数据
			SignStatDTO signStatItem = Optional.ofNullable(activity.getSignId()).map(signIdSignStatMap::get).orElse(null);
			boolean openSignUp = signStatItem != null && CollectionUtils.isNotEmpty(signStatItem.getSignUpIds());
			activity.setOpenSignUp(openSignUp);
			if (openSignUp) {
				activity.setSignedUpNum(Optional.ofNullable(signStatItem.getSignedUpNum()).orElse(0));
				activity.setPersonLimit(Optional.ofNullable(signStatItem.getLimitNum()).orElse(0));
			}
		}
	}

	/**封装管理者（管理员）
	 * @Description
	 * @author wwb
	 * @Date 2021-04-06 14:20:51
	 * @param activities
	 * @return void
	 */
	private void packageManager(List<Activity> activities) {
		if (CollectionUtils.isNotEmpty(activities)) {
			List<Integer> activityIds = activities.stream().map(Activity::getId).collect(Collectors.toList());
			// 查询配置的管理员列表
			List<ActivityManager> allActivityManagers = activityManagerQueryService.listByActivityId(activityIds);
			// 根据活动id分组
			Map<Integer, List<ActivityManager>> activityIdActivitiesMap = allActivityManagers.stream().collect(Collectors.groupingBy(ActivityManager::getActivityId));
			for (Activity activity : activities) {
				Integer activityId = activity.getId();
				List<ActivityManager> activityManagers = activityIdActivitiesMap.get(activityId);
				if (activityManagers == null) {
					activityManagers = Lists.newArrayList();
				}
				activity.setManagerUids(activityManagers.stream().map(ActivityManager::getUid).collect(Collectors.toList()));
			}
		}
	}

	/**分页查询管理的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-04-08 18:00:51
	 * @param page
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> pageManaged(Page<Activity> page, LoginUserDTO loginUser, String sw, String flag) {
		Integer marketId = marketQueryService.getMarketIdByFlag(loginUser.getFid(), flag);
		// 若flag不为空且市场id不存在，则查询结果为空
		if (StringUtils.isNotBlank(flag) && marketId == null) {
			page.setRecords(Lists.newArrayList());
			return page;
		}
		page = activityMapper.pageUserManaged(page, loginUser.getUid(), sw, marketId);
		packageActivitySignedStat(page);
		return page;
	}

	/**根据活动id查询活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-19 18:59:35
	 * @param activityId
	 * @return com.chaoxing.activity.model.Activity
	 */
	public Activity getById(Integer activityId) {
		Activity activity = activityMapper.getById(activityId);
		Optional.ofNullable(activity).map(Activity::getStartTime).ifPresent(v -> activity.setStartTimeStr(v.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS)));
		Optional.ofNullable(activity).map(Activity::getEndTime).ifPresent(v -> activity.setEndTimeStr(v.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS)));
		Optional.ofNullable(activity).map(Activity::getTimingReleaseTime).ifPresent(v -> activity.setTimingReleaseTimeStr(v.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS)));
		return activity;
	}

	/**根据门户pageId查询活动
	 * @Description
	 * @author wwb
	 * @Date 2020-12-10 18:14:27
	 * @param pageId
	 * @return com.chaoxing.activity.model.Activity
	 */
	public Activity getByPageId(Integer pageId) {
		return activityMapper.selectOne(new QueryWrapper<Activity>()
				.lambda()
				.eq(Activity::getPageId, pageId)
		);
	}

	/**根据门户websiteId查询活动
	 * @Description
	 * @author wwb
	 * @Date 2021-05-21 16:05:17
	 * @param websiteId
	 * @return com.chaoxing.activity.model.Activity
	 */
	public Activity getByWebsiteId(Integer websiteId) {
		return activityMapper.selectOne(new QueryWrapper<Activity>()
				.lambda()
				.eq(Activity::getWebsiteId, websiteId)
		);
	}

	/**根据报名签到id查询活动
	 * @Description
	 * @author wwb
	 * @Date 2020-12-30 20:38:23
	 * @param signId
	 * @return com.chaoxing.activity.model.Activity
	 */
	public Activity getBySignId(Integer signId) {
		if (signId == null) {
			return null;
		}
		return activityMapper.getBySignId(signId);
	}

	/**查询封面url为空的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-01-20 14:12:59
	 * @param
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listEmptyCoverUrl() {
		return activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda()
				.and(wrapper -> wrapper.isNull(Activity::getCoverUrl).or().eq(Activity::getCoverUrl, ""))
		);
	}

	/**分页查询已报名活动
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 20:30:46
	 * @param page
	 * @param loginUser
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
	 */
	public Page pageSignedUp(Page page, LoginUserDTO loginUser, String sw, String flag, Boolean loadWaitAudit) {
		Integer uid = loginUser.getUid();
		Integer fid = loginUser.getFid();
		List<Integer> marketIds = Lists.newArrayList();
		Integer marketId = marketQueryService.getMarketIdByFlag(fid, flag);
		if (marketId != null) {
			marketIds.add(marketId);
		}
		// 若flag不为空且市场id不存在，则查询结果为空
		if (StringUtils.isNotBlank(flag) && marketId == null) {
			page.setRecords(Lists.newArrayList());
			return page;
		}
		// 查询用户全部待审核或成功报名的报名签到信息
		Page signedUpPage = signApiService.pageUserSignedUp(new Page<>(1, Integer.MAX_VALUE), uid, sw);
		if (CollectionUtils.isNotEmpty(signedUpPage.getRecords())) {
			List<UserSignUpStatusStatDTO> signedUpRecords = JSONArray.parseArray(JSON.toJSONString(signedUpPage.getRecords()), UserSignUpStatusStatDTO.class);
			pageSignedUpActivities(page, signedUpRecords, marketIds, uid, loadWaitAudit);
		}
		packageActivitySignedStat(page);
		return page;
	}

	/**封装报名的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-20 18:15:26
	 * @param page
	 * @param marketIds
	 * @return void
	 */
	private void pageSignedUpActivities(Page page, List<UserSignUpStatusStatDTO> userSignUpStatuses, List<Integer> marketIds, Integer uid, Boolean loadWaitAudit) {
		List<ActivitySignedUpDTO> result = Lists.newArrayList();
		List<Integer> successSignIds = userSignUpStatuses.stream().filter(v -> Objects.equals(v.getUserSignUpStatus(), 1)).map(UserSignUpStatusStatDTO::getSignId).collect(Collectors.toList());
		List<Integer> waitAuditSignIds = userSignUpStatuses.stream().filter(v -> Objects.equals(v.getUserSignUpStatus(), 2)).map(UserSignUpStatusStatDTO::getSignId).collect(Collectors.toList());
		waitAuditSignIds.removeAll(successSignIds);
		int additionalTotal = 0;
		if (loadWaitAudit && CollectionUtils.isNotEmpty(waitAuditSignIds)) {
			Page<Activity> waitAuditPage= activityMapper.pageSignedUpActivities(new Page<>(1, Integer.MAX_VALUE), waitAuditSignIds, null, marketIds);
			result = activitiesConvert2ActivitySignedUp(waitAuditPage.getRecords(), userSignUpStatuses, uid);
			additionalTotal = result.size();
		}
		if (CollectionUtils.isNotEmpty(successSignIds)) {
			page = activityMapper.pageSignedUpActivities(page, successSignIds, null, marketIds);
		}
		List<ActivitySignedUpDTO> records = activitiesConvert2ActivitySignedUp(page.getRecords(), userSignUpStatuses, uid);
		result.addAll(records);

		page.setRecords(result);
		page.setTotal(page.getTotal() + additionalTotal);
	}


	/**门户我报名的活动查询
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-20 17:46:05
	 * @param page
	 * @param loginUser
	 * @param sw
	 * @param flag
	 * @param marketIds
	 * @param specificCurrOrg
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
	 */
	public Page mhPageSignedUp(Page page, LoginUserDTO loginUser, String sw, String flag, Integer activityClassifyId, List<Integer> marketIds, Integer specificCurrOrg) {
		Integer uid = loginUser.getUid();
		Integer fid = loginUser.getFid();
		Integer specificFid = Objects.equals(specificCurrOrg, 1) ? fid : null;
		if (StringUtils.isNotBlank(flag)) {
			// 若flag不为空且市场id不存在，则查询结果为空
			Integer marketId = marketQueryService.getMarketIdByFlag(fid, flag);
			if (marketId == null) {
				page.setRecords(Lists.newArrayList());
				return page;
			}
			marketIds = Lists.newArrayList(marketId);
		}

		Page signedUpPage = signApiService.pageUserSignedUp(new Page(1, Integer.MAX_VALUE), uid, sw, specificFid);
		if (CollectionUtils.isNotEmpty(signedUpPage.getRecords())) {
			mhPageSignedUpActivities(page, signedUpPage.getRecords(), activityClassifyId, marketIds, uid);
		}
		return page;
	}

	/**门户分页查询封装
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-17 11:08:09
	 * @param page
	 * @param records	UserSignUpStatusStatDTO 列表(由于从Page中获取，类型为Object，需要进行类型转换)
	 * @param activityClassifyId
	 * @param marketIds
	 * @return
	 */
	private void mhPageSignedUpActivities(Page page, List records, Integer activityClassifyId, List<Integer> marketIds, Integer uid) {
		if (CollectionUtils.isEmpty(records)) {
			return;
		}
		List<UserSignUpStatusStatDTO> userSignUpStatusStatuses = JSONArray.parseArray(JSON.toJSONString(records), UserSignUpStatusStatDTO.class);
		List<Integer> signIds = userSignUpStatusStatuses.stream().map(UserSignUpStatusStatDTO::getSignId).collect(Collectors.toList());
		page = activityMapper.pageSignedUpActivities(page, signIds, activityClassifyId, marketIds);
		List<ActivitySignedUpDTO> activitySignedUps = activitiesConvert2ActivitySignedUp(page.getRecords(), userSignUpStatusStatuses, uid);
		page.setRecords(activitySignedUps);
	}

	/**给活动包装用户报名状态信息, 转换成activitySignedUp实体
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-16 18:29:22
	 * @param activities
	 * @param records
	 * @return
	 */
	private List<ActivitySignedUpDTO> activitiesConvert2ActivitySignedUp(List<Activity> activities, List<UserSignUpStatusStatDTO> records, Integer uid) {
		Map<Integer, UserSignUpStatusStatDTO> signIdSignedUpMap = records.stream().collect(Collectors.toMap(UserSignUpStatusStatDTO::getSignId, v -> v, (v1, v2) -> {
			if (Objects.equals(v1.getUserSignUpStatus(), 1)) {
				return v1;
			} else if (Objects.equals(v2.getUserSignUpStatus(), 1)) {
				return v2;
			} else {
				return v2;
			}
		}));
		List<ActivitySignedUpDTO> activitySignedUps = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(activities)) {
			for (Activity activity : activities) {
				ActivitySignedUpDTO activitySignedUp = new ActivitySignedUpDTO();
				BeanUtils.copyProperties(activity, activitySignedUp);
				// 设置报名状态
				Integer signId = activitySignedUp.getSignId();
				UserSignUpStatusStatDTO signedUp = signIdSignedUpMap.get(signId);
				if (signedUp != null) {
					activitySignedUp.setSignUpId(signedUp.getSignUpId());
					activitySignedUp.setUserSignUpStatus(signedUp.getUserSignUpStatus());
				}
				activitySignedUp.setManagAble(activityValidationService.isManageAble(activity, uid));
				activitySignedUps.add(activitySignedUp);
			}
		}
		return activitySignedUps;
	}

	/**查询收藏的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 20:58:26
	 * @param page
	 * @param loginUser
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> pageCollected(Page page, LoginUserDTO loginUser, String sw, String flag) {
		Integer marketId = marketQueryService.getMarketIdByFlag(loginUser.getFid(), flag);
		// 若flag不为空且市场id不存在，则查询结果为空
		if (StringUtils.isNotBlank(flag) && marketId == null) {
			page.setRecords(Lists.newArrayList());
			return page;
		}
		page = activityMapper.pageCollectedActivityId(page, loginUser.getUid(), sw, marketId);
		packageActivitySignedStat(page);
		return page;
	}

	/**阅读测评的地址
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-28 15:18:08
	 * @param activity
	 * @return java.lang.String
	*/
	public String getReadingTestUrl(Activity activity) {
		return String.format(UrlConstant.READING_TEST_URL, activity.getReadingId(), activity.getReadingModuleId());
	}

	/**查询所有的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 18:32:13
	 * @param
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> list() {
		return activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda()
				.ne(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
		);
	}

	/**查询机构创建的指定flag的活动列表
	 * @Description
	 * @author wwb
	 * @Date 2021-04-19 10:46:24
	 * @param fid
	 * @param flag
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listOrgCreated(Integer fid, String flag) {
		// 先根据flag查询市场
		Integer marketId = marketQueryService.getMarketIdByFlag(fid, flag);
		return activityMapper.listOrgCreated(fid, marketId, flag);
	}

	public List<Integer> listByActivityDate(LocalDate date) {
		return activityMapper.listByActivityDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}

	/**根据来源的表单审批记录行id查询
	 * @Description
	 * @author wwb
	 * @Date 2021-05-11 16:13:28
	 * @param originTypeEnum
	 * @param origin
	 * @return com.chaoxing.activity.model.Activity
	 */
	public Activity getByOriginTypeAndOrigin(Activity.OriginTypeEnum originTypeEnum, String origin) {
		List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda()
				.eq(Activity::getOriginType, originTypeEnum.getValue())
				.eq(Activity::getOrigin, origin)
		);
		if (CollectionUtils.isNotEmpty(activities)) {
			return activities.get(0);
		}
		return null;
	}

	/**根据机构id, 给定的活动时间范围，查询在此范围内进行中的活动id列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-12 15:26:37
	 * @param fid
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listActivityIdsByFid(Integer fid, String startDate, String endDate) {
		return activityMapper.listOrgReleasedActivityIds(fid, startDate, endDate);
	}

	/**查询活动已报名用户id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-05-12 18:09:16
	 * @param activityId
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listSignedUpUid(Integer activityId) {
		Activity activity = getById(activityId);
		return listSignedUpUid(activity);
	}

	/**查询活动已报名用户id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-05-12 18:16:06
	 * @param activity
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listSignedUpUid(Activity activity) {
		List<Integer> uids = Lists.newArrayList();
		if (activity != null) {
			Integer signId = activity.getSignId();
			if (signId != null) {
				// 报名的uid列表
				uids = signApiService.listSignedUpUid(signId);
			}
		}
		return uids;
	}

	/**根据活动id查询已报名的用户
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-29 17:28:21
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.dto.manager.PassportUserDTO>
	 */
	public List<PassportUserDTO> listSignedUpUsers(Integer activityId) {
		Activity activity = getById(activityId);
		List<Integer> uids = listSignedUpUid(activity);
		List<PassportUserDTO> users = Lists.newArrayList();
		uids.forEach(uid -> {
			PassportUserDTO user = passportApiService.getByUid(uid);
			users.add(user);
		});

		return users;
	}


	/**根据活动id，查询已报名却未评价的用户id
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-14 10:50:04
	 * @param activity
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listNoRateSignedUpUid(Activity activity) {
		List<Integer> signedUpUids = listSignedUpUid(activity);
		return listNoRatingUid(activity.getId(), signedUpUids);
	}

	/**根据活动id，用户ids，过滤出未评价用户id
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-14 10:50:04
	 * @param activityId
	 * @param uids
	 * @return java.util.List<java.lang.Integer>
	 */
	private List<Integer> listNoRatingUid(Integer activityId, List<Integer> uids) {
		if (CollectionUtils.isEmpty(uids)) {
			return new ArrayList<>();
		}
		List<Integer> ratedUids = activityRatingDetailMapper.selectList(new QueryWrapper<ActivityRatingDetail>().lambda()
						.eq(ActivityRatingDetail::getActivityId, activityId)
						// 未删除的评论
						.eq(ActivityRatingDetail::getDeleted, Boolean.FALSE)
						.in(ActivityRatingDetail::getScorerUid, uids))
				.stream()
				.map(ActivityRatingDetail::getScorerUid)
				.collect(Collectors.toList());

		uids.removeAll(ratedUids);
		return uids;
	}

	/**针对创建机构、时间范围，对活动进行分页查询
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-25 16:31:54
	 * @param page
	 * @param fid
	 * @param startTimeStr
	 * @param endTimeStr
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 */
	public Page<Activity> activityPage(Page<Activity> page, Integer fid, String startTimeStr, String endTimeStr) {
		LambdaQueryWrapper<Activity> wrapper = new QueryWrapper<Activity>().lambda().eq(Activity::getCreateFid, fid);
		if (StringUtils.isNotBlank(startTimeStr)) {
			LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
			wrapper.gt(Activity::getStartTime, startTime);
		}
		if (StringUtils.isNotBlank(endTimeStr)) {
			LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
			wrapper.lt(Activity::getStartTime, endTime);
		}
		return activityMapper.selectPage(page, wrapper);
	}

	/**查询pageId不为空websiteId为空的活动id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-05-27 10:39:40
	 * @param
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listEmptyWebsiteIdActivityId() {
		List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda()
				.isNotNull(Activity::getPageId)
				.isNull(Activity::getWebsiteId)
				.select(Activity::getId)
		);
		if (CollectionUtils.isNotEmpty(activities)) {
			return activities.stream().map(Activity::getId).collect(Collectors.toList());
		}
		return Lists.newArrayList();
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-31 11:25:37
	 * @param signId
	 * @return com.chaoxing.activity.dto.sign.create.SignUpCreateParamDTO
	 */
	public SignUpCreateParamDTO getActivitySignUp(Integer signId) {
		if (signId == null) {
			return null;
		}
		SignCreateParamDTO signCreateParam = signApiService.getCreateById(signId);
		List<SignUpCreateParamDTO> signUps = Optional.ofNullable(signCreateParam).map(SignCreateParamDTO::getSignUps).orElse(Lists.newArrayList());
		if (CollectionUtils.isEmpty(signUps)) {
			return null;
		}
		return signUps.get(0);
	}

	/**根据活动id列表查询活动列表
	 * @Description
	 * @author wwb
	 * @Date 2021-06-06 20:56:29
	 * @param activityIds
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listByIds(List<Integer> activityIds) {
		List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda()
				.in(Activity::getId, activityIds)
		);
		if (CollectionUtils.isNotEmpty(activities)) {
			List<Integer> classifyIds = activities.stream().map(Activity::getActivityClassifyId).collect(Collectors.toList());
			List<Classify> classifies = classifyQueryService.listByIds(classifyIds);
			if (CollectionUtils.isNotEmpty(classifies)) {
				Map<Integer, Classify> classifyIdMap = classifies.stream().collect(Collectors.toMap(Classify::getId, v -> v, (v1, v2) -> v2));
				for (Activity activity : activities) {
					Integer activityClassifyId = activity.getActivityClassifyId();
					Classify classify = classifyIdMap.get(activityClassifyId);
					if (classify != null) {
						activity.setActivityClassifyName(classify.getName());
					}
				}
			}
		}
		return activities;
	}

	/**根据机构id, 给定的活动时间范围，查询在此范围内进行中的活动id列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-12 15:26:37
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listActivityIdsByFids(List<Integer> fids, String startDate, String endDate) {
		if (CollectionUtils.isEmpty(fids)) {
			return Lists.newArrayList();
		}
		return activityMapper.listOrgsReleasedActivityId(fids, startDate, endDate);
	}

	/**根据活动id查询活动详情
	 * @Description
	 * @author wwb
	 * @Date 2021-06-18 14:20:45
	 * @param activityId
	 * @return com.chaoxing.activity.model.ActivityDetail
	 */
	public ActivityDetail getDetailByActivityId(Integer activityId) {
		List<ActivityDetail> activityDetails = activityDetailMapper.selectList(new QueryWrapper<ActivityDetail>()
				.lambda()
				.eq(ActivityDetail::getActivityId, activityId)
		);
		return activityDetails.stream().findFirst().orElse(null);
	}

	/**根据活动ids查询活动详情
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-26 17:15:27
	 * @param activityIds
	 * @return com.chaoxing.activity.model.ActivityDetail
	 */
	public List<ActivityDetail> listDetailByActivityIds(List<Integer> activityIds) {
		if (CollectionUtils.isEmpty(activityIds)) {
			return Lists.newArrayList();
		}
		return activityDetailMapper.selectList(new QueryWrapper<ActivityDetail>()
				.lambda()
				.in(ActivityDetail::getActivityId, activityIds)
		);
	}

	/**根据作品征集id查询活动
	 * @Description
	 * @author wwb
	 * @Date 2021-06-21 16:36:04
	 * @param workId
	 * @return com.chaoxing.activity.model.Activity
	 */
	public Activity getByWorkId(Integer workId) {
		List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda()
				.eq(Activity::getWorkId, workId)
		);
		return activities.stream().findFirst().orElse(null);
	}

	/**对活动其他关联数据进一步查询封装
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-07-21 19:22:35
	 * @param activity
	 * @return com.chaoxing.activity.dto.activity.ActivityCreateParamDTO
	 */
	public ActivityCreateParamDTO packageActivityCreateParamByActivity(Activity activity) {
		if (activity == null) {
			return ActivityCreateParamDTO.buildDefault();
		}
		// activity -> activityCreateParamDTO
		Integer activityId = activity.getId();
		ActivityCreateParamDTO createParamDTO = ActivityCreateParamDTO.buildFromActivity(activity);
		// set 简介
		ActivityDetail activityDetail = getDetailByActivityId(activityId);
		createParamDTO.setIntroduction(activityDetail.getIntroduction());
		// set 自定义组件值对象列表
		List<ActivityComponentValueDTO> activityComponentValues = activityComponentValueService.listActivityComponentValues(activityId, activity.getTemplateId());
		createParamDTO.setActivityComponentValues(activityComponentValues);
		// set 考核管理id
		packageInspectConfig(createParamDTO, activityId);
		// 查询活动提醒
		ActivityPushReminder activityPushReminder = activityPushReminderService.getByActivityId(activityId);
		if (activityPushReminder != null && StringUtils.isNotBlank(activityPushReminder.getReceiveScope())) {
			activityPushReminder.setReceiveScopes(JSONArray.parseArray(activityPushReminder.getReceiveScope(), SignUpParticipateScopeDTO.class));
		} else {
			activityPushReminder = ActivityPushReminder.buildDefault(activityId);
		}
		createParamDTO.setActivityPushReminder(activityPushReminder);
		// 封装标签
		List<Integer> tagIds = tagQueryService.listActivityAssociateTagId(activityId);
		createParamDTO.setTagIds(tagIds);
		return createParamDTO;
	}

	private void packageInspectConfig(ActivityCreateParamDTO activityCreateParam, Integer activityId) {
		// set 考核管理id
		InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		if (inspectionConfig != null) {
			activityCreateParam.setInspectionConfigId(inspectionConfig.getId());
		}
		List<String> menus = activityMenuService.listMenus(activityId).stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList());
		activityCreateParam.setOpenInspectionConfig(menus.contains(ActivityMenuEnum.RESULTS_MANAGE.getValue()));
	}

	/**根据报名签到id列表和活动市场id统计正在进行中的活动数量
	 * @Description
	 * @author wwb
	 * @Date 2021-08-18 18:20:04
	 * @param marketId
	 * @param signIds
	 * @return java.lang.Integer
	 */
	public Integer countIngActivityNumBySignIds(Integer marketId, List<Integer> signIds) {
		return activityMapper.selectCount(new LambdaQueryWrapper<Activity>()
				.eq(Activity::getMarketId, marketId)
				.in(Activity::getSignId, signIds)
				.ne(Activity::getStatus, Activity.StatusEnum.ENDED.getValue())
		);
	}

	/**获取活动的字段code与名称的关系
	 * @Description
	 * @author wwb
	 * @Date 2021-08-18 20:11:10
	 * @param activityId
	 * @return java.util.Map<java.lang.String,java.lang.String>
	 */
	public Map<String, String> getFieldCodeNameRelation(Integer activityId) {
		Activity activity = getById(activityId);
		return getFieldCodeNameRelation(activity);
	}

	/**获取活动的字段code与名称的关系
	 * @Description
	 * @author wwb
	 * @Date 2021-08-18 20:16:42
	 * @param activity
	 * @return java.util.Map<java.lang.String,java.lang.String>
	 */
	public Map<String, String> getFieldCodeNameRelation(Activity activity) {
		// 系统组件code与名称的关联
		Map<String, String> fieldCodeNameRelation = componentQueryService.getSystemComponentCodeNameRelation();
		if (activity == null) {
			return fieldCodeNameRelation;
		}
		Integer templateId = activity.getTemplateId();
		if (templateId == null) {
			String activityFlag = activity.getActivityFlag();
			templateId = templateQueryService.getSystemTemplateIdByActivityFlag(Activity.ActivityFlagEnum.fromValue(activityFlag));
		}
		if (templateId == null) {
			return fieldCodeNameRelation;
		}
		// 查询模版关联的组件
		List<TemplateComponent> templateComponents = templateComponentQueryService.listTemplateComponentByTemplateId(templateId);
		Map<Integer, String> componentIdNameRelation = templateComponents.stream().collect(Collectors.toMap(TemplateComponent::getComponentId, TemplateComponent::getName, (v1, v2) -> v2));
		// 没有关联的组件使用系统默认组件的名称来填充
		List<Integer> componentIds = Optional.ofNullable(templateComponents).orElse(Lists.newArrayList()).stream().map(TemplateComponent::getComponentId).collect(Collectors.toList());
		Map<Integer, String> componentIdCodeRelation;
		if (CollectionUtils.isNotEmpty(componentIds)) {
			List<Component> components = componentQueryService.listByIds(componentIds);
			componentIdCodeRelation = components.stream().collect(Collectors.toMap(Component::getId, v -> StringUtils.isBlank(v.getCode()) ? "" : v.getCode()));
		} else {
			componentIdCodeRelation = Maps.newHashMap();
		}
		componentIdCodeRelation.forEach((k, v) -> {
			fieldCodeNameRelation.put(v, componentIdNameRelation.get(k));
		});
		return fieldCodeNameRelation;
	}

	/**查询万能表单创建的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-26 16:42:46
	 * @param formId
	 * @param formUserId
	 * @return boolean
	 */
	public Activity getByWfwFormUserId(Integer formId, Integer formUserId) {
		if (formId == null || formUserId == null) {
			return null;
		}
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>().eq(Activity::getOrigin, formId).eq(Activity::getOriginFormUserId, formUserId)).stream().findFirst().orElse(null);
	}

	/**查询marketId下非删除状态的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-01 16:42:19
	 * @param marketId
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listByMarketId(Integer marketId) {
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.eq(Activity::getMarketId, marketId)
				.ne(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
				.select(Activity::getId, Activity::getCreateUid, Activity::getCreateFid));
	}

	/**查询机构创建的作品征集列表（未删除的活动）
	 * @Description
	 * @author wwb
	 * @Date 2021-09-02 10:51:30
	 * @param fid
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listOrgCreatedWorkId(Integer fid) {
		List<Activity> onlyWorkIds = activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.eq(Activity::getCreateFid, fid)
				.ne(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
				.select(Activity::getWorkId)
		);
		return Optional.ofNullable(onlyWorkIds).orElse(Lists.newArrayList()).stream().filter(v -> v != null).map(Activity::getWorkId).filter(v -> v != null).collect(Collectors.toList());
	}

	/**查询机构下一级（活动的下一级class、school、region）创建的作品征集id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-09-07 20:04:04
	 * @param superiorFid 上级fid
	 * @param workId
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listOrgJuniorCreatedWorkId(Integer superiorFid, Integer workId) {
		Activity activity = getByWorkId(workId);
		if (activity == null) {
			return Lists.newArrayList();
		}
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(activity.getActivityFlag());
		String queryActivityFlag;
		switch (activityFlagEnum) {
			case CLASS:
				queryActivityFlag = Activity.ActivityFlagEnum.SCHOOL.getValue();
				superiorFid = activity.getCreateFid();
				break;
			case SCHOOL:
			case REGION:
				queryActivityFlag = Activity.ActivityFlagEnum.REGION.getValue();
				break;
			default:
				queryActivityFlag = "";
		}
		List<Integer> workIds = activityMapper.listErdosCustomOrgCreatedWorkId(superiorFid, activity.getCreateFid(), queryActivityFlag, activity.getActivityClassifyId());
		workIds.remove(workId);
		return workIds;
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-18 16:44:08
	 * @param queryParam
	 * @return com.chaoxing.activity.dto.export.ExportDataDTO
	 */
	public ExportDataDTO packageExportData(ActivityManageQueryDTO queryParam, LoginUserDTO exportUser) {
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listMarketShowTableFieldDetail(queryParam.getMarketId(), TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		Page<Activity> page = listManaging(new Page<>(1, Integer.MAX_VALUE), queryParam, exportUser);
		return ExportDataDTO.builder()
				.headers(listActivityHeader(tableFieldDetails))
				.data(listData(page.getRecords(), tableFieldDetails))
				.build();
	}

	private List<List<String>> listActivityHeader(List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> headers = Lists.newArrayList();
		headers.add(Lists.newArrayList("活动ID"));
		for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
			List<String> header = Lists.newArrayList();
			header.add(tableFieldDetail.getName());
			headers.add(header);
		}
		return headers;
	}


	private String valueToString(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	/**获取数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-01 16:19:14
	 * @param records
	 * @param tableFieldDetails
	 * @return java.util.List<java.util.List<java.lang.String>>
	 */
	private List<List<String>> listData(List<Activity> records, List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> data = Lists.newArrayList();
		if (CollectionUtils.isEmpty(records)) {
			return data;
		}
		for (Activity record : records) {
			List<String> itemData = Lists.newArrayList();
			PassportUserDTO createUser = passportApiService.getByUid(record.getCreateUid());
			String createOrgName = passportApiService.getOrgName(record.getCreateFid());
			// 活动id
			itemData.add(valueToString(record.getId()));
			for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
				String code = tableFieldDetail.getCode();
				switch (code) {
					case "cover":
						String coverUrl = record.getCoverUrl();
						if (StringUtils.isBlank(coverUrl) && StringUtils.isNotBlank(record.getCoverCloudId())) {
							coverUrl = cloudApiService.buildImageUrl(record.getCoverCloudId());
						}
						itemData.add(coverUrl);
						break;
					case "name":
						itemData.add(record.getName());
						break;
					case "createUserName":
						itemData.add(createUser.getRealName());
						break;
					case "createOrgName":
						itemData.add(createOrgName);
						break;
					case "signedUpNum":
						itemData.add(valueToString(record.getSignedUpNum()));
						break;
					case "status":
						itemData.add(Activity.StatusEnum.fromValue(record.getStatus()).getName());
						break;
					case "poster":
						itemData.add(UrlConstant.getPosterUrl(record.getId()));
						break;
					case "startTime":
						itemData.add(Optional.ofNullable(record.getStartTime()).map(v -> v.format(DateUtils.DATE_MINUTE_TIME_FORMATTER)).orElse(null));
						break;
					case "endTime":
						itemData.add(Optional.ofNullable(record.getEndTime()).map(v -> v.format(DateUtils.DATE_MINUTE_TIME_FORMATTER)).orElse(null));
						break;
					case "personLimit":
						itemData.add(valueToString(record.getPersonLimit()));
						break;
					case "signedInNum":
						itemData.add(valueToString(record.getSignedInNum()));
						break;
					case "signedInRate":
						itemData.add(valueToString(record.getSignedInRate()));
						break;
					case "rateNum":
						itemData.add(valueToString(record.getRateNum()));
						break;
					case "rateScore":
						itemData.add(valueToString(record.getRateScore()));
						break;
					case "qualifiedNum":
						itemData.add(valueToString(record.getQualifiedNum()));
						break;
					case "activityClassify":
						itemData.add(record.getActivityClassifyName());
						break;
					default:

				}
			}
			data.add(itemData);
		}
		return data;
	}

	/**查询未开始的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-11-03 16:48:47
	 * @param
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listNotStart() {
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.in(Activity::getStatus, Lists.newArrayList(Activity.StatusEnum.WAIT_RELEASE.getValue(), Activity.StatusEnum.RELEASED.getValue()))
		);
	}

	/**活动应该开始但是状态不是进行中的活动
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-06 15:09:20
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	*/
	public List<Activity> listOngoingButStatusError() {
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.in(Activity::getStatus, Lists.newArrayList(Activity.StatusEnum.RELEASED.getValue(), Activity.StatusEnum.ENDED.getValue()))
				.eq(Activity::getReleased, true)
				.le(Activity::getStartTime, LocalDateTime.now())
		);
	}

	/**查询未结束的活动
	 * @Description
	 * @author wwb
	 * @Date 2021-11-03 16:48:57
	 * @param
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listNotEnd() {
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.in(Activity::getStatus, Lists.newArrayList(Activity.StatusEnum.WAIT_RELEASE.getValue(), Activity.StatusEnum.RELEASED.getValue(), Activity.StatusEnum.ONGOING.getValue()))
		);
	}

	/**查询活动结束但是状态不对的活动列表
	 * @Description 活动应该结束但是状态不是已结束的
	 * @author wwb
	 * @Date 2022-01-06 15:06:08
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	*/
	public List<Activity> listEndedButStatusError() {
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.in(Activity::getStatus, Lists.newArrayList(Activity.StatusEnum.RELEASED.getValue(), Activity.StatusEnum.ONGOING.getValue()))
				.eq(Activity::getReleased, true)
				.le(Activity::getEndTime, LocalDateTime.now())
		);
	}

	/**查询机构创建的活动列表
	 * @Description
	 * @author wwb
	 * @Date 2021-11-17 17:07:12
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listByFid(Integer fid) {
		return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.eq(Activity::getCreateFid, fid)
		);
	}


	/**根据现有活动activityId，查询活动信息，克隆出一个活动创建实例
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-18 17:21:22
	 * @param originActivityId
	 * @return com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO
	 */
	public ActivityCreateParamDTO cloneActivity(Integer originActivityId) {
		// 查询活动
		Activity originActivity = getById(originActivityId);
		if (originActivity == null) {
			return ActivityCreateParamDTO.buildDefault();
		}
		// 克隆活动
		ActivityCreateParamDTO activityCreateParam = ActivityCreateParamDTO.cloneFromActivity(originActivity);
		activityCreateParam.setOriginActivityId(originActivityId);
		// 克隆活动的活动开始时间当前开始，一个月后结束
		LocalDateTime now = LocalDateTime.now();
		activityCreateParam.setStartTimeStamp(DateUtils.date2Timestamp(now));
		activityCreateParam.setEndTimeStamp(DateUtils.date2Timestamp(now.plusMonths(1)));
		// 开启了定时发布，则设置定时发布时间
		if (activityCreateParam.getTimingRelease()) {
			activityCreateParam.setTimingReleaseTimeStamp(DateUtils.date2Timestamp(now));
		}
		// 判断作品征集是否开启，开启则创建新的作品征集，并设置克隆活动新的作品征集信息
		if (activityCreateParam.getOpenWork()) {
			activityCreateParam.setWorkId(workApiService.createDefault(originActivity.getCreateUid(), originActivity.getCreateFid()));
		}
		//活动简介
		ActivityDetail originActivityDetail = getDetailByActivityId(originActivityId);
		activityCreateParam.setIntroduction(originActivityDetail.getIntroduction());
		// 查询活动自定义组件值对象列表， 且置空自定义组件值列表中的活动id
		List<ActivityComponentValueDTO> activityComponentValues = activityComponentValueService.listActivityComponentValues(originActivityId, originActivity.getTemplateId());
		if (CollectionUtils.isNotEmpty(activityComponentValues)) {
			activityComponentValues.forEach(v -> v.setActivityId(null));
			activityCreateParam.setActivityComponentValues(activityComponentValues);
		}
		// 启用的报名条件
		List<Integer> enableSucTplComponentIds = signUpConditionService.listActivityEnabledTemplateComponentId(originActivityId);
		activityCreateParam.setSucTemplateComponentIds(enableSucTplComponentIds);
		// 启用的自定义应用模板组件id列表
		List<Integer> enableCustomAppTplComponentIds = customAppConfigQueryService.listEnabledActivityCustomAppTplComponentId(originActivityId);
		activityCreateParam.setCustomAppEnableTplComponentIds(enableCustomAppTplComponentIds);
		// 启用的报名条件列表
		List<SignUpCondition> signUpConditions = signUpConditionService.listEditActivityConditions(originActivityId, originActivity.getTemplateId());
		activityCreateParam.setSignUpConditions(signUpConditions);
		// 考核配置
		packageInspectConfig(activityCreateParam, originActivityId);
		// 封装标签
		List<Integer> tagIds = tagQueryService.listActivityAssociateTagId(originActivityId);
		activityCreateParam.setTagIds(tagIds);
		return activityCreateParam;
	}

	/**填充活动的标签名称列表
	 * @Description
	 * @author wwb
	 * @Date 2021-11-25 09:42:22
	 * @param activities
	 * @return void
	 */
	public void fillTagNames(List<Activity> activities) {
		if (CollectionUtils.isEmpty(activities)) {
			return;
		}
		Map<Integer, Activity> activityIdObjectMap = activities.stream().collect(Collectors.toMap(Activity::getId, v -> v, (v1, v2) -> v2));
		Set<Integer> activityIds = activityIdObjectMap.keySet();
		List<ActivityTagNameDTO> activityTagNames = tagQueryService.listActivityTagNameByActivityIds(new ArrayList<>(activityIds));
		Map<Integer, List<ActivityTagNameDTO>> activityIdTagNamesMap = activityTagNames.stream().collect(Collectors.groupingBy(ActivityTagNameDTO::getActivityId));
		for (Activity activity : activities) {
			Integer activityId = activity.getId();
			List<ActivityTagNameDTO> activityAssociatedTagNames = activityIdTagNamesMap.get(activityId);
			List<String> tagNames = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(activityAssociatedTagNames)) {
				tagNames = activityAssociatedTagNames.stream().map(ActivityTagNameDTO::getTagName).collect(Collectors.toList());
			}
			activity.setTagNames(tagNames);
		}
	}

	/**分页查询机构创建或发布到机构下的活动
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-01 11:18:22
	 * @param activityQuery
	 * @return 
	 */
	public Page createParticipateActivityPage(Page page, ActivityCreateParticipateQueryDTO activityQuery) {
		activityQuery.init();
		return activityMapper.createParticipateActivityPage(page, activityQuery);
	}

	/**查询区域创建的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-02 16:06:20
	 * @param activityReleasePlatformActivityQuery
	 * @return java.util.List<com.chaoxing.activity.dto.activity.query.result.ActivityReleasePlatformActivityQueryResultDTO>
	*/
	public List<ActivityReleasePlatformActivityQueryResultDTO> listAreaCreated(ActivityReleasePlatformActivityQueryDTO activityReleasePlatformActivityQuery) {
		String code = activityReleasePlatformActivityQuery.getCode();
		Integer fid = activityReleasePlatformActivityQuery.getFid();
		if (StringUtils.isBlank(code) && fid == null) {
			throw new BusinessException("区域编码和fid不能同时为空");
		}
		List<Integer> fids = Lists.newArrayList();
		if (StringUtils.isNotBlank(code)) {
			List<WfwAreaDTO> wfwAreas = wfwAreaApiService.listByCode(code);
			if (CollectionUtils.isNotEmpty(wfwAreas)) {
				fids.addAll(wfwAreas.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList()));
			}
		}
		if (fid != null) {
			fids.add(fid);
		}
		if (CollectionUtils.isEmpty(fids)) {
			return Lists.newArrayList();
		}
		LambdaQueryWrapper<Activity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.in(Activity::getCreateFid, fids);
		if (StringUtils.isNotBlank(activityReleasePlatformActivityQuery.getFlag())) {
			lambdaQueryWrapper.eq(Activity::getActivityFlag, activityReleasePlatformActivityQuery.getFlag());
		}
		if (activityReleasePlatformActivityQuery.getStartTimestamp() != null) {
			LocalDateTime startTime = DateUtils.timestamp2Date(activityReleasePlatformActivityQuery.getStartTimestamp());
			lambdaQueryWrapper.ge(Activity::getStartTime, startTime);
		}
		if (activityReleasePlatformActivityQuery.getEndTimestamp() != null) {
			LocalDateTime endTime = DateUtils.timestamp2Date(activityReleasePlatformActivityQuery.getEndTimestamp());
			lambdaQueryWrapper.le(Activity::getStartTime, endTime);
		}

		List<Activity> activities = activityMapper.selectList(lambdaQueryWrapper);
		List<Integer> activityIds = activities.stream().map(Activity::getId).collect(Collectors.toList());
		// 根据活动id列表查询参与范围
		List<WfwAreaDTO> scopeWfwAreas = activityScopeQueryService.listByActivityIds(activityIds);
		Map<Integer, List<WfwAreaDTO>> activityIdWfwAreasMap = scopeWfwAreas.stream().collect(Collectors.groupingBy(WfwAreaDTO::getActivityId));
		return ActivityReleasePlatformActivityQueryResultDTO.build(activities, activityIdWfwAreasMap);
	}

	/**查询所有预告中的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-24 15:17:18
	 * @param
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 */
	public List<Activity> listAllForecastActivity(ActivityQueryDTO activityQuery) {
		activityQuery.setStatusList(Lists.newArrayList(2));
		List<String> tagNames = activityQuery.getTags();
		if (CollectionUtils.isNotEmpty(tagNames)) {
			List<Tag> tags = tagQueryService.listByNames(tagNames);
			activityQuery.setTagIds(tags.stream().map(Tag::getId).collect(Collectors.toList()));
		}
		Page<Activity> page = activityMapper.pageParticipate(new Page<>(1, Integer.MAX_VALUE), activityQuery);
		return page.getRecords();
	}


	/**查询机构或市场下活动列表
	 *
	 * marketId 不为空，优先查询市场下的活动
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-07 17:13:48
	 * @param marketId
	 * @param fid
	 * @return
	 */
	public List<Activity> listActivityIdsByMarketIdOrFid(Integer marketId, Integer fid) {
		if (marketId == null && fid == null) {
			return Lists.newArrayList();
		}
		return activityMapper.listActivityIdsByMarketIdOrFid(marketId, fid);
	}
}