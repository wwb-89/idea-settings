package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.stat.*;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.mapper.ActivityStatMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityStat;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatQueryService
 * @description
 * @blame wwb
 * @date 2021-01-13 19:29:58
 */
@Slf4j
@Service
public class ActivityStatQueryService {

	@Resource
	private ActivityMapper activityMapper;

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private MhApiService mhApiService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityStatMapper activityStatMapper;

	/**机构参与的活动的pageId列表
	 * @Description
	 * @author wwb
	 * @Date 2021-01-13 19:20:40
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listOrgParticipatedActivityPageId(List<Integer> fids) {
		List<Integer> result = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(fids)) {
			List<Integer> pageIds = activityMapper.listOrgParticipatedActivityPageId(fids);
			result = pageIds.stream().filter(v -> v != null).collect(Collectors.toList());
		}
		return result;
	}

	/**获取活动统计时间范围
	 * @Description
	 * @author wwb
	 * @Date 2021-04-15 19:45:39
	 * @param activityId
	 * @return com.chaoxing.activity.dto.TimeScopeDTO
	*/
	public TimeScopeDTO getActivityStatTimeScope(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		return getActivityStatTimeScope(activity);
	}

	/**获取活动统计时间范围
	 * @Description
	 * @author wwb
	 * @Date 2021-04-16 09:43:05
	 * @param activity
	 * @return com.chaoxing.activity.dto.TimeScopeDTO
	*/
	public TimeScopeDTO getActivityStatTimeScope(Activity activity) {
		return getTimeScope(activity.getStartTime(), activity.getEndTime());
	}

	/**
	 * 获取时间范围
	 *
	 * @param startTime
	 * @param endTime
	 * @return com.chaoxing.activity.dto.TimeScopeDTO
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-11 16:06:41
	 */
	public TimeScopeDTO getTimeScope(LocalDateTime startTime, LocalDateTime endTime) {
		LocalDateTime now = LocalDateTime.now();
		endTime = now.isAfter(endTime) ? endTime : now;
		return TimeScopeDTO.builder()
				.startTime(startTime)
				.endTime(endTime)
				.build();
	}

	/**活动统计
	 * @Description
	 * @author wwb
	 * @Date 2021-04-15 20:03:05
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.dto.stat.ActivityStatDTO
	*/
	public ActivityStatDTO activityStat(Integer activityId, LoginUserDTO loginUser) {
		ActivityStatDTO activityStat = ActivityStatDTO.buildDefault();
		Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
		return activityStat(activityStat, activity);
	}

	/**活动统计
	 * @Description
	 * @author wwb
	 * @Date 2021-04-15 20:03:05
	 * @param activityId
	 * @return com.chaoxing.activity.dto.stat.ActivityStatDTO
	*/
	public ActivityStatDTO activityStat(Integer activityId) {
		ActivityStatDTO activityStat = ActivityStatDTO.buildDefault();
		Activity activity = activityQueryService.getById(activityId);
		return activityStat(activityStat, activity);
	}

	/**活动统计
	* @Description
	* @author huxiaolong
	* @Date 2021-05-10 18:31:42
	* @param activityStat
	* @param activity
	* @return com.chaoxing.activity.dto.stat.ActivityStatDTO
	*/
	public ActivityStatDTO activityStat(ActivityStatDTO activityStat, Activity activity) {
		TimeScopeDTO activityStatTimeScope = getActivityStatTimeScope(activity);
		List<String> daily = DateUtils.listEveryDay(activityStatTimeScope.getStartTime(), activityStatTimeScope.getEndTime());
		activityStat.setDaily(daily);
		if (CollectionUtils.isNotEmpty(daily)) {
			String startTimeStr = activityStatTimeScope.getStartTime().format(DateUtils.FULL_TIME_FORMATTER);
			String endTimeStr = activityStatTimeScope.getEndTime().format(DateUtils.FULL_TIME_FORMATTER);
			Integer pageId = activity.getPageId();
			if (pageId != null) {
				// pv
				Integer websiteId = mhApiService.getWebsiteIdByPageId(pageId);
				Integer pv = mhApiService.countWebsitePv(websiteId);
				activityStat.setPv(pv);
				// pv趋势
				List<MhViewNumDailyStatDTO> pvTrend = mhApiService.statWebsiteDailyViewNum(websiteId, startTimeStr, endTimeStr);
				List<DailyStatDTO> pvDailyStats = Lists.newArrayList();
				if (CollectionUtils.isNotEmpty(pvTrend)) {
					for (MhViewNumDailyStatDTO mhViewNumDailyStat : pvTrend) {
						DailyStatDTO dailyStat = new DailyStatDTO();
						BeanUtils.copyProperties(mhViewNumDailyStat, dailyStat);
						pvDailyStats.add(dailyStat);
					}
				}
				activityStat.setPvTrend(fullConvert2(daily, pvDailyStats));
			}
			// 报名签到统计
			SignActivityStatDTO signActivityStat = signApiService.singleActivityStat(activity.getSignId(), startTimeStr, endTimeStr);
			activityStat.setSignedUpNum(signActivityStat.getSignedUpNum());
			activityStat.setSignedInNum(signActivityStat.getSignedInNum());
			activityStat.setSignUpTrend(fullConvert2(daily, signActivityStat.getSignUpTrend()));
			activityStat.setSignInTrend(fullConvert2(daily, signActivityStat.getSignInTrend()));
		}
		return activityStat;
	}

	private List<DailyStatDTO> fullConvert2(List<String> daily, List<DailyStatDTO> origins) {
		List<DailyStatDTO> result = Lists.newArrayList();
		Map<String, String> dateValueMap = Maps.newHashMap();
		for (DailyStatDTO origin : origins) {
			String dateStr = origin.getDateStr();
			String value = origin.getValue();
			if (StringUtils.isBlank(value)) {
				value = "0";
			}
			dateValueMap.put(dateStr, value);
		}
		for (String day : daily) {
			String value = dateValueMap.get(day);
			if (StringUtils.isBlank(value)) {
				value = "0";
			}
			DailyStatDTO dailyStat = DailyStatDTO.builder()
					.dateStr(day)
					.value(value)
					.build();
			result.add(dailyStat);
		}
		return result;
	}

	/**
	 * 根据机构fid查询机构下的活动统计信息
	 *
	 * @param fid
	 * @return com.chaoxing.activity.dto.stat.ActivityOrgStatDTO
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-11 15:21:34
	 */
	public ActivityOrgStatDTO orgActivityStat(Integer fid) {
		return orgActivityStat(fid, null, null);
	}

	/**
	 * 根据机构fid、startTime、endTime查询机构下的活动统计信息
	 * 浏览量为所有统计记录pv总和，pv趋势为每日所有统计记录pv总和
	 * 报名人数以所有活动最新统计记录的signedUpNum总和
	 * 签到人数以所有活动最新统计记录的signedInNum总和
	 *
	 * @param fid
	 * @param startDate
	 * @param endDate
	 * @return com.chaoxing.activity.dto.stat.ActivityOrgStatDTO
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-11 15:21:34
	 */
	public ActivityOrgStatDTO orgActivityStat(Integer fid, String startDate, String endDate) {
		ActivityOrgStatDTO result = ActivityOrgStatDTO.buildDefault();
		// 根据fid查询机构下的活动，活动按照类型进行分组并统计数量
		List<ActivityClassifyDTO> activityClassifyList = activityMapper.listActivityGroupByClassifyId(fid);
		if (CollectionUtils.isEmpty(activityClassifyList)) {
			return result;
		}
		result.setClassifyStatList(activityClassifyList);

		// 获取机构下的activityIds
		List<Integer> activityIds = activityMapper.selectList(new QueryWrapper<Activity>()
				.lambda().select(Activity::getId)
				.eq(Activity::getCreateFid, fid))
				.stream().map(Activity::getId)
				.collect(Collectors.toList());

		boolean timeScopeQuery = StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate);
		// 查询结果根据statDate升序排列
		List<ActivityStat> activityStatList = activityStatMapper.listActivityStat(startDate, endDate, activityIds);

		if (CollectionUtils.isNotEmpty(activityStatList)) {
			int statSize = activityStatList.size();
			Integer pvSum = 0, signedInNumSum = 0, signedUpNumSum = 0;
			// key 为活动id， value 为统计记录，不重复且最新的记录map
			Map<Integer, ActivityStat> activityStatMap = Maps.newHashMap();

			// 统计日期-浏览量map
			Map<String, Integer> datePvMap = Maps.newHashMap();
			for (ActivityStat activityStat : activityStatList) {
				Integer activityId = activityStat.getActivityId();
				ActivityStat statItem = activityStatMap.get(activityId);
				// 若 activityStatMap 中不含activityId 的统计记录，或者统计记录的统计时间在下一个元素之前，则替换
				if (statItem == null || statItem.getStatDate().isBefore(activityStat.getStatDate())) {
					activityStatMap.put(activityId, activityStat);
				}

				String dateStr = activityStat.getStatDate().format(DateUtils.DAY_DATE_TIME_FORMATTER);
				Integer pvVal = Optional.ofNullable(datePvMap.get(dateStr)).orElse(0);

				pvVal += activityStat.getPv();

				datePvMap.put(dateStr, pvVal);
				// 计算浏览量总数
				pvSum += activityStat.getPv();
			}

			// 统计日期-签到数和map
			Map<String, Integer> dateSignedInNumMap = Maps.newHashMap();
			// 统计日期-报名数和map
			Map<String, Integer> dateSignedUpNumMap = Maps.newHashMap();
			for (Map.Entry<Integer, ActivityStat> entry : activityStatMap.entrySet()) {
				ActivityStat item = entry.getValue();

				String dateStr = item.getStatDate().format(DateUtils.DAY_DATE_TIME_FORMATTER);

				// 获取map中当前统计日期的签到/报名数量
				Integer signedInVal = Optional.ofNullable(dateSignedInNumMap.get(dateStr)).orElse(0);
				Integer signedUpVal = Optional.ofNullable(dateSignedUpNumMap.get(dateStr)).orElse(0);

				// 更新当前统计日期的签到/报名数量
				signedInVal += item.getSignedInNum();
				signedUpVal += item.getSignedUpNum();

				dateSignedInNumMap.put(dateStr, signedInVal);
				dateSignedUpNumMap.put(dateStr, signedUpVal);

				signedInNumSum += item.getSignedInNum();
				signedUpNumSum += item.getSignedUpNum();
			}

			List<String> daily;
			if (timeScopeQuery) {
				daily = DateUtils.listEveryDay(activityStatList.get(0).getStatDate(),
						activityStatList.get(statSize - 1).getStatDate());
			} else {
				daily = DateUtils.listEveryDay(LocalDate.parse(startDate), LocalDate.parse(endDate));
			}

			result.setActivityNum(activityIds.size());
			result.setPv(pvSum);
			result.setPvTrend(fullConvert2(daily, datePvMap));
			result.setSignedInNum(signedInNumSum);
			result.setSignedUpNum(signedUpNumSum);
			result.setDaily(daily);
			result.setSignInTrend(fullConvert2(daily, dateSignedInNumMap));
			result.setSignUpTrend(fullConvert2(daily, dateSignedUpNumMap));
		}
		return result;
	}

	private List<DailyStatDTO> fullConvert2(List<String> daily, Map<String, Integer> origins) {
		List<DailyStatDTO> result = Lists.newArrayList();
		for (String dateStr : daily) {
			Integer val = Optional.ofNullable(origins.get(dateStr)).orElse(0);
			String value = String.valueOf(val);
			DailyStatDTO dailyStat = DailyStatDTO.builder()
					.dateStr(dateStr)
					.value(value)
					.build();
			result.add(dailyStat);
		}
		return result;
	}
}
