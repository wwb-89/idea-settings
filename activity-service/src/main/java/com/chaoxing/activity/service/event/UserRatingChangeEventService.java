package com.chaoxing.activity.service.event;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserRatingQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
	private UserStatSummaryHandleService userStatSummaryService;
	@Resource
	private UserActionQueueService userActionQueueService;

	/**用户评价变更
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 18:15:22
	 * @param activityRatingDetail
	 * @return void
	*/
	public void change(ActivityRatingDetail activityRatingDetail) {
		Integer activityId = activityRatingDetail.getActivityId();
		Integer scorerUid = activityRatingDetail.getScorerUid();
		// 更新用户的评价数量
		userStatSummaryService.updateUserRatingNum(scorerUid);
		// 更新用户行为详情
		UserActionEnum userAction;
		if (activityRatingDetail.getDeleted()) {
			userAction = UserActionEnum.DELETE_RATING;
		} else {
			userAction = UserActionEnum.RATING;
		}
		LocalDateTime updateTime = activityRatingDetail.getUpdateTime();
		userActionQueueService.push(new UserActionQueueService.QueueParamDTO(scorerUid, activityId, UserActionTypeEnum.RATING, userAction, String.valueOf(activityRatingDetail.getId()), updateTime));
	}

}