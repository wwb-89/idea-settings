package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityTypeDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.enums.ActivityQueryDateEnum;
import com.chaoxing.activity.util.enums.ActivityTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	private ActivityValidationService activityValidationService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;
	
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
		page = activityMapper.listParticipate(page, activityQuery);
		return page;
	}

	/**活动日历查询
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-03 16:07:47
	 * @param page
	 * @param mhActivityCalendarQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	public Page<Activity> listActivityCalendarParticipate(Page<Activity> page, MhActivityCalendarQueryDTO mhActivityCalendarQuery) {
		page = activityMapper.listActivityCalendarParticipate(page, mhActivityCalendarQuery);
		return page;
	}
	
	/**查询机构创建的
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 21:48:23
	 * @param page
	 * @param fid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	public Page<Activity> listCreated(Page<Activity> page, Integer fid) {
		page = activityMapper.listCreated(page, fid);
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
		String date = activityQuery.getDate();
		date = Optional.ofNullable(date).orElse("");
		ActivityQueryDateEnum activityQueryDateEnum = ActivityQueryDateEnum.fromValue(date);
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
		List<ActivityTypeDTO> result = new ArrayList<>();
		ActivityTypeEnum[] values = ActivityTypeEnum.values();
		for (ActivityTypeEnum value : values) {
			ActivityTypeDTO activityType = ActivityTypeDTO.builder()
					.name(value.getName())
					.value(value.getValue())
					.build();
			result.add(activityType);
		}
		return result;
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
		List<Integer> fids = new ArrayList<>();
		Integer fid = loginUser.getFid();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(fid);
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(fid);
		}
		activityManageQuery.setFids(fids);
		page = activityMapper.listManaging(page, activityManageQuery);
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
		Activity activity = activityValidationService.activityExist(activityId);
		Optional.ofNullable(activity).map(Activity::getStartTime).ifPresent(v -> activity.setStartTimeStr(v.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS)));
		Optional.ofNullable(activity).map(Activity::getEndTime).ifPresent(v -> activity.setEndTimeStr(v.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS)));
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

}