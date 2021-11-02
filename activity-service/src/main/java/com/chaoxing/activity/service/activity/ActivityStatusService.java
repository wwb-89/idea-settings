package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.service.event.ActivityDataChangeEventService;
import com.chaoxing.activity.service.event.ActivityReleaseStatusChangeEventService;
import com.chaoxing.activity.service.event.ActivityStatusChangeEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**活动状态更新服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusUpdateService
 * @description
 * @blame wwb
 * @date 2020-12-10 19:35:04
 */
@Slf4j
@Service
public class ActivityStatusService {

	@Resource
	private ActivityMapper activityMapper;

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityDataChangeEventService activityChangeEventService;
	@Resource
	private ActivityMarketService activityMarketService;
	@Resource
	private ActivityStatusChangeEventService activityStatusChangeEventService;
	@Resource
	private ActivityReleaseStatusChangeEventService activityReleaseStatusChangeEventService;

	/**活动状态更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-06 15:05:42
	 * @param activityId
	 * @return void
	*/
	public void statusUpdate(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		statusUpdate(activity);
	}
	/**状态更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:41:44
	 * @param activity
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void statusUpdate(Activity activity) {
		if (activity != null) {
			Integer oldStatus = activity.getStatus();
			Integer activityId = activity.getId();
			Activity.calAndSetActivityStatus(activity);
			activityMapper.update(null, new UpdateWrapper<Activity>()
					.lambda()
					.eq(Activity::getId, activityId)
					.set(Activity::getStatus, activity.getStatus())
			);
			// 更新所有关联该活动的市场的活动状态
			List<ActivityMarket> activityMarkets = activityMarketService.listByActivityId(activityId);
			if (CollectionUtils.isNotEmpty(activityMarkets)) {
				activityMarkets.forEach(v -> activityMarketService.updateActivityStatus(v.getMarketId(), activity));
			}
			activityStatusChangeEventService.statusChange(activity, oldStatus);
		}
	}

	/**更新活动发布状态
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-06 15:14:01
	 * @param activity
	 * @return void
	*/
	public void updateReleaseStatus(Activity activity) {
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activity.getId())
				.set(Activity::getReleased, activity.getReleased())
				.set(Activity::getReleaseTime, activity.getReleaseTime())
				.set(Activity::getReleaseUid, activity.getReleaseUid())
		);
		// 更新活动状态
		statusUpdate(activity);
		// 活动状态改变
		activityReleaseStatusChangeEventService.releaseStatusChange(activity);
	}

}
