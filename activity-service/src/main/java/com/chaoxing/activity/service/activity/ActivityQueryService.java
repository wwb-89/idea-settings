package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.UserResultDTO;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.ActivitySignedUpDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpAbleSignDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignUpStatusStatDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.mapper.*;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.ActivityFlagCodeService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.component.ComponentQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.DateFormatConstant;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.chaoxing.activity.util.enums.ActivityQueryDateScopeEnum;
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
	private SignUpConditionEnableMapper signUpConditionEnableMapper;
	@Resource
	private ComponentQueryService componentQueryService;
	@Resource
	private MarketQueryService marketQueryService;
	@Resource
	private ActivityFlagCodeService activityFlagCodeService;
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
		if (currentUid != null && Optional.ofNullable(signUpAble).orElse(false)) {
			page.setSize(Integer.MAX_VALUE);
			page = activityMapper.pageParticipate(page, activityQuery);
			List<Activity> records = page.getRecords();
			// 只查询能报名的
			List<Integer> signIds = Optional.ofNullable(records).orElse(Lists.newArrayList()).stream().map(Activity::getSignId).filter(v -> v != null).collect(Collectors.toList());
			List<SignUpAbleSignDTO> signUpAbleSigns = signApiService.listSignUpAbleSign(currentUid, signIds);
			List<Activity> activities = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(signUpAbleSigns)) {
				Map<Integer, SignUpAbleSignDTO> signIdSignUpAbleSignMap = signUpAbleSigns.stream().collect(Collectors.toMap(SignUpAbleSignDTO::getSignId, v -> v, (v1, v2) -> v2));
				for (Activity record : records) {
					SignUpAbleSignDTO signUpAbleSign = signIdSignUpAbleSignMap.get(record.getSignId());
					if (signUpAbleSign != null) {
						record.setHasSignUp(true);
						record.setSignUpStatus(signUpAbleSign.getSignUpStatus());
						record.setSignUpStatusDescribe(signUpAbleSign.getSignUpStatusDescribe());
						activities.add(record);
					}
				}
			}
			page.setRecords(activities);
			page.setTotal(activities.size());
		} else {
			page = activityMapper.pageParticipate(page, activityQuery);
		}
		return page;
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
		page = activityMapper.pageFlag(page, activityQuery);
		return page;
	}

	/**枫叶查询机构能参与的活动（机构在参与范围内）
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-09 16:36:38
	 * @param page
	 * @param activityQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	public Page<Activity> ListOrgParticipate(Page<Activity> page, ActivityQueryDTO activityQuery) {
		calDateScope(activityQuery);
		page = activityMapper.pageOrgParticipate(page, activityQuery);
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
		return activityMapper.pageErdosParticipate(page, activityQuery);
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

	/**计算查询的时间范围
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 14:21:57
	 * @param activityQuery
	 * @return void
	*/
	private void calDateScope(ActivityQueryDTO activityQuery) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dateScope = activityQuery.getDateScope();
		dateScope = Optional.ofNullable(dateScope).orElse("");
		ActivityQueryDateScopeEnum activityQueryDateEnum = ActivityQueryDateScopeEnum.fromValue(dateScope);
		LocalDate now = LocalDate.now();
		String minDateStr;
		String maxDateStr;
		switch (activityQueryDateEnum) {
			case ALL:
				minDateStr = "";
				maxDateStr = "";
				break;
			case NEARLY_A_MONTH:
				minDateStr = now.plusMonths(-1).format(dateTimeFormatter);
				maxDateStr = "";
				break;
			case NEARLY_THREE_MONTH:
				minDateStr = now.plusMonths(-3).format(dateTimeFormatter);
				maxDateStr = "";
				break;
			case NEARLY_SIX_MONTH:
				minDateStr = now.plusMonths(-6).format(dateTimeFormatter);
				maxDateStr = "";
				break;
			case NEARLY_A_YEAR:
				minDateStr = now.plusYears(-1).format(dateTimeFormatter);
				maxDateStr = "";
				break;
			default:
				// 更早
				minDateStr = "";
				maxDateStr = now.plusYears(-1).format(dateTimeFormatter);
		}
		activityQuery.setMinDateStr(minDateStr);
		activityQuery.setMaxDateStr(maxDateStr);
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
		// 市场id为空时，查找flag对应的区域code下的fids
		if (activityManageQuery.getMarketId() == null && StringUtils.isNotBlank(activityManageQuery.getActivityFlag())) {
			String code = activityFlagCodeService.getCodeByFlag(activityManageQuery.getActivityFlag());
			List<WfwAreaDTO> wfwAreas = wfwAreaApiService.listByCode(code);
			WfwAreaDTO currWfwArea = wfwAreas.stream().filter(v -> Objects.equals(activityManageQuery.getFid(), v.getFid())).findFirst().orElse(null);
			if (currWfwArea != null) {
				List<Integer> subFids = wfwAreas.stream().filter(v -> StringUtils.startsWith(v.getCode(), currWfwArea.getCode())).map(WfwAreaDTO::getFid).collect(Collectors.toList());
				activityManageQuery.setFids(subFids);
			}
		}
		if (strict.compareTo(1) == 0) {
			// 严格模式
			activityManageQuery.setCreateUid(loginUser.getUid());
			activityManageQuery.setCreateWfwfid(activityManageQuery.getFid());
			page = activityMapper.pageCreated(page, activityManageQuery);
		} else {
			if (StringUtils.isNotBlank(activityManageQuery.getCode())) {
				List<Integer> fids = wfwAreaApiService.listSubFid(activityManageQuery.getFid());
				activityManageQuery.setFids(fids);
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
			List<SignStatDTO> signStats = signApiService.statSignSignedUpNum(signIds);
			signIdSignStatMap = signStats.stream().collect(Collectors.toMap(SignStatDTO::getId, v -> v, (v1, v2) -> v2));
		}
		for (Activity activity : activities) {
			// 活动报名签到状态数据
			SignStatDTO signStatItem = Optional.ofNullable(activity.getSignId()).map(signIdSignStatMap::get).orElse(null);
			activity.setSignedUpNum(Optional.ofNullable(signStatItem).map(SignStatDTO::getSignedUpNum).orElse(0));
			activity.setPersonLimit(Optional.ofNullable(signStatItem).map(SignStatDTO::getSignedUpNum).orElse(0));
			// 活动统计数据
			ActivityStatSummaryDTO summaryItem = statSummaryMap.get(activity.getId());
			activity.setSignedInNum(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getSignedInNum).orElse(0));
			activity.setSignedInRate(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getSignInRate).orElse(new BigDecimal(0)));
			activity.setRateNum(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getRateNum).orElse(0));
			activity.setRateScore(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getRateScore).orElse(new BigDecimal(0)));
			activity.setQualifiedNum(Optional.ofNullable(summaryItem).map(ActivityStatSummaryDTO::getQualifiedNum).orElse(0));
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
		return activityMapper.pageUserMarketManaged(page, loginUser.getUid(), sw, marketId);
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
	 * @param specificCurrOrg 是否查询指定机构下的所有活动，0：所有，1：查询指定机构下的所有
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
	*/
	public Page pageSignedUp(Page page, LoginUserDTO loginUser, String sw, String flag, Integer specificCurrOrg) {
		Integer uid = loginUser.getUid();
		Integer fid = loginUser.getFid();
		Integer specificFid = Objects.equals(specificCurrOrg, 1) ? fid : null;
		page = signApiService.pageUserSignedUp(page, uid, sw, specificFid);
		List records = page.getRecords();
		if (CollectionUtils.isNotEmpty(records)) {
			List<Integer> signIds = Lists.newArrayList();
			Map<Integer, UserSignUpStatusStatDTO> signIdSignedUpMap = Maps.newHashMap();
			for (Object record : records) {
				JSONObject jsonObject = (JSONObject) record;
				UserSignUpStatusStatDTO signedUp = JSON.parseObject(jsonObject.toJSONString(), UserSignUpStatusStatDTO.class);
				signIds.add(signedUp.getSignId());
				signIdSignedUpMap.put(signedUp.getSignId(), signedUp);
			}
			List<ActivitySignedUpDTO> activitySignedUps = Lists.newArrayList();
			Integer marketId = marketQueryService.getMarketIdByFlag(fid, flag);
			// 若flag不为空且市场id不存在，则查询结果为空
			if (StringUtils.isNotBlank(flag) && marketId == null) {
				page.setRecords(Lists.newArrayList());
				return page;
			}
			List<Activity> activities = activityMapper.listByMarketSignIds(signIds, marketId);
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
					activitySignedUps.add(activitySignedUp);
				}
			}
			page.setRecords(activitySignedUps);
		}
		return page;
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
		return activityMapper.pageMarketCollectedActivityId(page, loginUser.getUid(), sw, marketId);
	}

	/**获取活动管理url
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 19:11:37
	 * @param activityId
	 * @return java.lang.String
	*/
	public String getActivityManageUrl(Integer activityId) {
		return String.format(UrlConstant.ATIVITY_MANAGE_URL, activityId);
	}

	/**活动评价地址
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-16 11:12:37
	 * @param activityId
	 * @return java.lang.String
	*/
	public String getActivityRatingUrl(Integer activityId) {
		return String.format(UrlConstant.ACTIVITY_RATING_URL, activityId);
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
		// set 报名条件
		List<Integer> signUpConditionEnables = signUpConditionEnableMapper.selectList(new QueryWrapper<SignUpConditionEnable>()
				.lambda()
				.eq(SignUpConditionEnable::getActivityId, activityId))
				.stream().map(SignUpConditionEnable::getTemplateComponentId).collect(Collectors.toList());
		createParamDTO.setSucTemplateComponentIds(signUpConditionEnables);
		// set 考核管理id
		InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		if (inspectionConfig != null) {
			createParamDTO.setInspectionConfigId(inspectionConfig.getId());
		}
		List<String> menus = activityMenuService.listMenus(activityId).stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList());
		createParamDTO.setOpenInspectionConfig(menus.contains(ActivityMenuEnum.RESULTS_MANAGE.getValue()));
		return createParamDTO;
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

	/**判断是否存在表单创建的活动
	* @Description
	* @author huxiaolong
	* @Date 2021-08-26 16:42:46
	* @param formId
	* @param formUserId
	* @return boolean
	*/
    public Activity getActivityByOriginAndFormUserId(Integer formId, Integer formUserId) {
    	if (formId == null || formUserId == null) {
    		return null;
		}
    	return activityMapper.selectList(new LambdaQueryWrapper<Activity>().eq(Activity::getOrigin, formId).eq(Activity::getOriginFormUserId, formUserId)).stream().findFirst().orElse(null);
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
	 * @param fid
	 * @param workId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listOrgJuniorCreatedWorkId(Integer fid, Integer workId) {
		Activity activity = getByWorkId(workId);
		if (activity == null) {
			return Lists.newArrayList();
		}
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(activity.getActivityFlag());
		String queryActivityFlag;
		switch (activityFlagEnum) {
			case CLASS:
				queryActivityFlag = Activity.ActivityFlagEnum.SCHOOL.getValue();
				break;
			case SCHOOL:
			case REGION:
				queryActivityFlag = Activity.ActivityFlagEnum.REGION.getValue();
				break;
			default:
				queryActivityFlag = "";
		}
		return activityMapper.listErdosCustomOrgCreatedWorkId(fid, activity.getCreateFid(), queryActivityFlag, activity.getActivityClassifyId());
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
//					case "dualSelect":
//						itemData.add()
//						break;
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
}