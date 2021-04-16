package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.stat.ActivityStatDTO;
import com.chaoxing.activity.dto.stat.DailyStatDTO;
import com.chaoxing.activity.dto.stat.MhViewNumDailyStatDTO;
import com.chaoxing.activity.dto.stat.SignActivityStatDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endTime = now.isAfter(activity.getEndTime()) ? activity.getEndTime() : now;
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

}
