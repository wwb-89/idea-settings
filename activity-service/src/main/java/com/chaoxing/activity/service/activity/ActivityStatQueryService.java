package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.stat.ActivityStatDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
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
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime endTime = activity.getEndTime();
		LocalDateTime now = LocalDateTime.now();
		return TimeScopeDTO.builder()
				.startTime(startTime)
				.endTime(now.isAfter(endTime) ? endTime : now)
				.build();
	}

	/**活动统计
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-15 20:03:05
	 * @param activityId
	 * @return com.chaoxing.activity.dto.stat.ActivityStatDTO
	*/
	public ActivityStatDTO activityStat(Integer activityId) {
		return null;
	}

}
