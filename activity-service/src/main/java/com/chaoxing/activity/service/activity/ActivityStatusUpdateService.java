package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.DateUtils;
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
public class ActivityStatusUpdateService {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityHandleService activityHandleService;

	/**计算活动状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-10 19:36:50
	 * @param activity
	 * @return java.lang.Integer
	*/
	public Integer calActivityStatus(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime endTime = activity.getEndTime();
		LocalDateTime now = LocalDateTime.now();
		boolean guessEnded = now.isAfter(endTime);
		boolean guessOnGoing = (now.isAfter(startTime) || now.isEqual(startTime)) && (now.isBefore(endTime) || now.isEqual(endTime));
		if (activity.getReleased()) {
			if (guessEnded) {
				// 已结束
				return Activity.StatusEnum.ENDED.getValue();
			}
			// 已发布的活动才处理状态
			if (guessOnGoing) {
				return Activity.StatusEnum.ONGOING.getValue();
			} else {
				return Activity.StatusEnum.RELEASED.getValue();
			}
		} else {
			return Activity.StatusEnum.WAIT_RELEASE.getValue();
		}
	}

	/**状态更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:41:44
	 * @param activityId
	 * @param l
	 * @return void
	*/
	public boolean statusUpdate(Integer activityId, long l) {
		LocalDateTime time = DateUtils.timestamp2Date(l);
		LocalDateTime now = LocalDateTime.now();
		if (time.compareTo(now) <= 0) {
			// 更新活动状态
			Activity activity = activityQueryService.getById(activityId);
			if (activity != null) {
				Integer status = calActivityStatus(activity);
				activityHandleService.updateActivityStatus(activityId, status);
			}
			return true;
		}
		return false;
	}

}
