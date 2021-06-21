package com.chaoxing.activity.service.event;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.user.UserActionDetailQueueService;
import com.chaoxing.activity.service.queue.user.UserRatingQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户评价变更事件服务
 * @author wwb
 * @version ver 1.0
 * @className UserRatingChangeEventService
 * @description
 * @blame wwb
 * @date 2021-03-26 18:13:44
 */
@Slf4j
@Service
public class UserRatingChangeEventService {

	@Resource
	private UserRatingQueueService userRatingQueueService;
	@Resource
	private UserStatSummaryHandleService userStatSummaryService;
	@Resource
	private UserActionDetailQueueService userActionDetailQueueService;

	/**用户评价变更
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 18:15:22
	 * @param uid
	 * @param activity
	 * @return void
	*/
	public void change(Integer uid, Activity activity) {
		Integer activityId = activity.getId();
		Integer signId = activity.getSignId();
		if (signId != null) {
			userRatingQueueService.push(UserRatingQueueService.QueueParamDTO.builder().uid(uid).signId(signId).build());
		}
		// 更新用户的评价数量
		userStatSummaryService.updateUserRatingNum(uid);
		// 更新用户行为详情
		userActionDetailQueueService.push(UserActionDetailQueueService.QueueParamDTO.builder().uid(uid).activityId(activityId).userActionType(UserActionTypeEnum.RATING).build());
	}

}