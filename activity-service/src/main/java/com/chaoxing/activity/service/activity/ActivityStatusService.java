package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.event.ActivityChangeEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
	private ActivityHandleService activityHandleService;
	@Resource
	private ActivityChangeEventService activityChangeEventService;


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
	public void statusUpdate(Activity activity) {
		if (activity != null) {
			Activity.StatusEnum status = Activity.calActivityStatus(activity);
			activityHandleService.updateActivityStatus(activity.getId(), status);
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
		activityChangeEventService.releaseStatusChange(activity);
	}

}
