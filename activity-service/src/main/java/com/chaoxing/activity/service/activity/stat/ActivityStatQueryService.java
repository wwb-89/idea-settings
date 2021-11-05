package com.chaoxing.activity.service.activity.stat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.admin.ActivityRegionStatQueryDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatQueryDTO;
import com.chaoxing.activity.dto.stat.*;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.mapper.ActivityStatMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityStat;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
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
import java.util.*;
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
	@Resource
	private WfwAreaApiService wfwAreaApiService;

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
//		todo 暂时屏蔽校验
//		Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
		Activity activity = activityValidationService.activityExist(activityId);
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
	 * 根据机构fid查询机构下的活动统计信息
	 * @param fid
	 * @return com.chaoxing.activity.dto.stat.ActivityOrgStatDTO
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-11 15:21:34
	 */
	public ActivityOrgStatDTO orgActivityStat(Integer fid, String startDate, String endDate) {
		// 根据机构id, 给定的活动时间范围，查询在此范围内进行中的活动id列表
		List<Integer> activityIds = activityQueryService.listActivityIdsByFid(fid, startDate, endDate);
		return packageActivityStat(activityIds, startDate, endDate, null);
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-11 14:29:49
	 * @param fid
	 * @return com.chaoxing.activity.dto.stat.ActivityOrgStatDTO
	 */
	public ActivityOrgStatDTO regionalActivityStat(Integer fid) {
		return regionalActivityStat(fid, null, null);
	}

	/**
	 * 根据机构fid查询机构下所有区域的机构的活动统计信息
	 * @param fid
	 * @param startDate
	 * @param endDate
	 * @return com.chaoxing.activity.dto.stat.ActivityOrgStatDTO
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-11 15:21:34
	 */
	public ActivityOrgStatDTO regionalActivityStat(Integer fid, String startDate, String endDate) {
		List<WfwAreaDTO> regionalOrgList = wfwAreaApiService.listByFid(fid);
		List<Integer> fids = regionalOrgList.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
		// 根据机构id集合, 给定的活动时间范围，查询在此范围内进行中的活动id列表
		List<Integer> activityIds = activityQueryService.listActivityIdsByFids(fids, startDate, endDate);
		return packageActivityStat(activityIds, startDate, endDate, fids.size());
	}

	/**
	 * 根据活动id集合及时间范围，对活动进行统计
	 * 浏览量为所有统计记录pv总和，pv趋势为每日所有统计记录pv总和
	 * 报名人数以所有活动最新统计记录的signedUpNum总和
	 * 签到人数以所有活动最新统计记录的signedInNum总和
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-11 14:10:32
	 * @param activityIds
	 * @param startDate
	 * @param endDate
	 * @return com.chaoxing.activity.dto.stat.ActivityOrgStatDTO
	 */
	private ActivityOrgStatDTO packageActivityStat(List<Integer> activityIds, String startDate, String endDate, Integer affiliationNum) {
		ActivityOrgStatDTO result = ActivityOrgStatDTO.buildDefault();
		result.setAffiliationNum(Optional.ofNullable(affiliationNum).orElse(0));
		if (CollectionUtils.isEmpty(activityIds)) {
			return result;
		}
		result.setActivityIds(activityIds);
		result.setActivityNum(activityIds.size());

		// 根据fid查询机构下的活动，活动按照类型进行分组并统计数量
		List<ActivityClassifyDTO> activityClassifyList = activityMapper.listActivityGroupByClassifyId(activityIds);
		result.setClassifyStatList(activityClassifyList);

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

				pvVal += activityStat.getPvIncrement();

				datePvMap.put(dateStr, pvVal);
				// 计算浏览量总数
				pvSum += activityStat.getPvIncrement();
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
				signedInVal += item.getSignedInIncrement();
				signedUpVal += item.getSignedUpIncrement();

				dateSignedInNumMap.put(dateStr, signedInVal);
				dateSignedUpNumMap.put(dateStr, signedUpVal);

				signedInNumSum += item.getSignedInIncrement();
				signedUpNumSum += item.getSignedUpIncrement();
			}

			LocalDate startDaily = activityStatList.get(0).getStatDate();
			LocalDate endDaily = activityStatList.get(statSize - 1).getStatDate();
			if (StringUtils.isNotBlank(startDate)) {
				startDaily = LocalDate.parse(startDate);
			}
			if (StringUtils.isNotBlank(endDate)) {
				endDaily = LocalDate.parse(endDate);
			}
			List<String> daily = DateUtils.listEveryDay(startDaily, endDaily);

			result.setPv(pvSum);
			result.setPvTrend(fullConvert2(daily, datePvMap));
			result.setSignedInNum(signedInNumSum);
			result.setSignedUpNum(signedUpNumSum);
			result.setDaily(daily);
			result.setSignInTrend(fullConvert2(daily, dateSignedInNumMap));
			result.setSignUpTrend(fullConvert2(daily, dateSignedUpNumMap));
		}
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			result.setDaily(DateUtils.listEveryDay(LocalDate.parse(startDate), LocalDate.parse(endDate)));
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

	public List<ActivityStat> listTopActivity(ActivityStatQueryDTO statQueryParams) {
		if (CollectionUtils.isEmpty(statQueryParams.getActivityIds())) {
			return new ArrayList<>();
		}

		String orderField = statQueryParams.getOrderField() == null ? null : statQueryParams.getOrderField().getValue();
		String orderType = statQueryParams.getOrderType() == null ? null : statQueryParams.getOrderType().getValue();
		List<ActivityStat> topActivityList = activityStatMapper.listTopActivity(statQueryParams, orderField, orderType);
		int rank = 0;
		for (ActivityStat item : topActivityList) {
			item.setRank(++rank);
		}
		return topActivityList;
	}

	/**根据报名签到id列表统计活动数量
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-27 16:20:13
	 * @param signIds
	 * @return java.lang.Integer
	*/
	public Integer countActivityNumBySignIds(List<Integer> signIds) {
		return activityMapper.selectCount(new QueryWrapper<Activity>()
				.lambda()
				.in(Activity::getSignId, signIds)
				.ne(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
		);
	}

	/**根据统计日期，查询活动id的统计记录
	* @Description
	* @author huxiaolong
	* @Date 2021-06-09 14:47:25
	* @param activityId
	* @param statDate
	* @return com.chaoxing.activity.model.ActivityStat
	*/
	public ActivityStat getActivityStatByStatDate(Integer activityId, LocalDate statDate) {
		List<ActivityStat> activityStatList = activityStatMapper.selectList(new QueryWrapper<ActivityStat>()
				.lambda()
				.eq(ActivityStat::getActivityId, activityId)
				.eq(ActivityStat::getStatDate, statDate));
		if (CollectionUtils.isEmpty(activityStatList)) {
			return null;
		}
		return activityStatList.get(0);
	}

	private List<ActivityRegionalStatDTO> convert2RegionalStat(Map<Integer, ActivityRegionalStatDTO> resultMap, Map<Integer, List<Integer>> regionOrgsMap, List<ActivityStat> regionalActivityStats) {
		for (Map.Entry<Integer, List<Integer>> entry : regionOrgsMap.entrySet()) {
			Integer fid = entry.getKey();
			List<Integer> affiliationIds = entry.getValue();

			for (ActivityStat stat : regionalActivityStats) {
				if (affiliationIds.contains(stat.getFid())) {
					ActivityRegionalStatDTO item = resultMap.get(fid);
					Integer pv = item.getPv();
					Integer signedUpNum = item.getSignedUpNum();
					Integer signedInNum = item.getSignedInNum();
					Integer activityNum = item.getActivityNum();
					pv += Optional.ofNullable(stat.getPv()).orElse(0);
					signedUpNum += Optional.ofNullable(stat.getSignedUpNum()).orElse(0);
					signedInNum += Optional.ofNullable(stat.getSignedInNum()).orElse(0);
					++activityNum;

					item.setActivityNum(activityNum);
					item.setSignedInNum(signedInNum);
					item.setSignedUpNum(signedUpNum);
					item.setPv(pv);

					resultMap.put(fid, item);
				}
			}
		}
		return Lists.newArrayList(resultMap.values());
	}

	private List<ActivityRegionalStatDTO> convert2RegionalStat(Map<Integer, ActivityRegionalStatDTO> resultMap, List<ActivityStat> regionalActivityStats) {
		for (ActivityStat stat : regionalActivityStats) {
			ActivityRegionalStatDTO item = resultMap.get(stat.getFid());
			Integer pv = item.getPv();
			Integer signedUpNum = item.getSignedUpNum();
			Integer signedInNum = item.getSignedInNum();
			Integer activityNum = item.getActivityNum();
			pv += Optional.ofNullable(stat.getPv()).orElse(0);
			signedUpNum += Optional.ofNullable(stat.getSignedUpNum()).orElse(0);
			signedInNum += Optional.ofNullable(stat.getSignedInNum()).orElse(0);
			++activityNum;

			item.setActivityNum(activityNum);
			item.setSignedInNum(signedInNum);
			item.setSignedUpNum(signedUpNum);
			item.setPv(pv);

			resultMap.put(stat.getFid(), item);
		}

		return Lists.newArrayList(resultMap.values());
	}

	/**当前机构下区域活动统计
	* @Description
	* @author huxiaolong
	* @Date 2021-06-11 18:14:25
	* @param queryParams
	* @return java.util.List<com.chaoxing.activity.dto.stat.ActivityRegionalStatDTO>
	*/
	public List<ActivityRegionalStatDTO> listRegionStatDetail(ActivityRegionStatQueryDTO queryParams) {
		Integer nodeId = queryParams.getRegionId();
		Integer fid = queryParams.getFid();
		String startDate = queryParams.getStartDate();
		String endDate = queryParams.getEndDate();
		// 区域查询
		return regionActivityStatQuery(nodeId, fid, startDate, endDate);
	}

	/**当前机构下区域活动统计
	* @Description
	* @author huxiaolong
	* @Date 2021-06-15 11:08:13
	* @param nodeId
	* @param startDate
	* @param endDate
	* @return java.util.List<com.chaoxing.activity.dto.stat.ActivityRegionalStatDTO>
	*/
	private List<ActivityRegionalStatDTO> regionActivityStatQuery(Integer nodeId, Integer fid, String startDate, String endDate) {
		List<WfwAreaDTO> regionalArchitectures = wfwAreaApiService.listByFid(fid);
		// 机构fid 对应着子节点的fid列表
		Map<Integer, List<Integer>> wfwRegionChildrenFidMap = Maps.newHashMap();
		// 区域fid 对应着对应的下属机构的fid列表
		Map<Integer, List<Integer>> regionOrgsMap = Maps.newHashMap();
		Map<Integer, ActivityRegionalStatDTO> resultMap = Maps.newHashMap();

		for (WfwAreaDTO item : regionalArchitectures) {
			wfwRegionChildrenFidMap.computeIfAbsent(item.getFid(), k -> Lists.newArrayList());
			for (WfwAreaDTO it: regionalArchitectures) {
				if (it.getPid() != null && Objects.equals(it.getPid(), item.getId())) {
					wfwRegionChildrenFidMap.get(item.getFid()).add(it.getFid());
				}
			}
			// 查找当前节点的区域节点
			if (item.getPid() != null && Objects.equals(item.getPid(), nodeId)) {
				Integer regionFid = item.getFid();
				regionOrgsMap.put(regionFid, Lists.newArrayList());
				resultMap.put(regionFid, ActivityRegionalStatDTO.buildDefault(regionFid, item.getName()));

			}
		}
		List<Integer> fids = Lists.newArrayList();
		// 从机构id 对应着子节点的fid列表 中， 获取区域id 对应着对应的下属机构的fid列表
		for (Map.Entry<Integer, List<Integer>> entry : regionOrgsMap.entrySet()) {
			Integer regionFid = entry.getKey();
			List<Integer> values = entry.getValue();
			searchRegionOrgFids(regionFid, values, wfwRegionChildrenFidMap);
			fids.addAll(values);

		}
		List<ActivityStat> regionalActivityStats =  activityStatMapper.listActivityStatByFids(fids, startDate, endDate);
		return convert2RegionalStat(resultMap, regionOrgsMap, regionalActivityStats);
	}

	/**根据区域fid查找下属机构的fid集合
	* @Description
	* @author huxiaolong
	* @Date 2021-06-15 12:06:53
	* @param regionFid
	* @param values
	* @param wfwRegionChildrenMap
	* @return void
	*/
	private void searchRegionOrgFids(Integer regionFid, List<Integer> values, Map<Integer, List<Integer>> wfwRegionChildrenMap) {
		List<Integer> childIds = wfwRegionChildrenMap.get(regionFid);
		if (CollectionUtils.isNotEmpty(childIds)) {
			values.addAll(childIds);
			for (Integer childId : childIds) {
				searchRegionOrgFids(childId, values, wfwRegionChildrenMap);
			}
			wfwRegionChildrenMap.get(regionFid);
		}
	}

	/**当前机构下下属机构各自的活动统计汇总
	* @Description 
	* @author huxiaolong
	* @Date 2021-06-11 18:14:42
	* @param queryParams
	* @return java.util.List<com.chaoxing.activity.dto.stat.ActivityRegionalStatDTO>
	*/
	public List<ActivityRegionalStatDTO> listRegionOrgStatDetail(ActivityRegionStatQueryDTO queryParams) {
		Integer fid = queryParams.getFid();
		String startDate = queryParams.getStartDate();
		String endDate = queryParams.getEndDate();
		// 根据fid查询下属所有机构
		List<WfwAreaDTO> regionalArchitectures = wfwAreaApiService.listByFid(fid);
		// 若当前机构没有下属机构，则返回空
		if (regionalArchitectures.size() == 1 && Objects.equals(regionalArchitectures.get(0).getFid(), fid)) {
			return null;
		}

		List<OrgDTO> orgList = Lists.newArrayList();
		for (WfwAreaDTO region : regionalArchitectures) {
			if (!Objects.equals(region.getFid(), fid)) {
				orgList.add(OrgDTO.builder()
						.fid(region.getFid())
						.name(region.getName())
						.build()) ;
			}
		}
		List<Integer> fids = Lists.newArrayList();
		Map<Integer, ActivityRegionalStatDTO> resultMap = Maps.newHashMap();
		for (OrgDTO region : orgList) {
			Integer regionFid = region.getFid();
			String name = region.getName();
			fids.add(regionFid);
			resultMap.put(regionFid, ActivityRegionalStatDTO.buildDefault(regionFid, name));
		}
		List<ActivityStat> regionalActivityStats =  activityStatMapper.listActivityStatByFids(fids, startDate, endDate);
		return convert2RegionalStat(resultMap, regionalActivityStats);
	}

	/**根据活动ids查询活动对应的浏览量
	* @Description
	* @author huxiaolong
	* @Date 2021-08-03 15:37:28
	* @param activityIds
	* @return java.util.List<com.chaoxing.activity.model.ActivityStat>
	*/
	public List<ActivityStat> listActivityPvByActivityIds(List<Integer> activityIds) {
		if (CollectionUtils.isEmpty(activityIds)) {
			return Lists.newArrayList();
		}
		return activityStatMapper.listActivityPvByActivityIds(activityIds);
	}
}
