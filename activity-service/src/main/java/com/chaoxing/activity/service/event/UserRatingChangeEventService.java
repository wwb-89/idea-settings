package com.chaoxing.activity.service.event;

import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueue;
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
	private UserActionRecordQueue userActionRecordQueue;

	/**用户评价变更
	 * @Description
	 * 新增、删除评价触发用户行为的改变
	 * @author wwb
	 * @Date 2021-03-26 18:15:22
	 * @param activityRatingDetail
	 * @return void
	*/
	public void change(ActivityRatingDetail activityRatingDetail) {
		Integer activityId = activityRatingDetail.getActivityId();
		Integer uid = activityRatingDetail.getScorerUid();
		// 更新用户的评价数量
		userStatSummaryService.updateUserRatingNum(uid, activityId);
		// 更新用户行为记录
		UserActionEnum userAction;
		if (activityRatingDetail.getDeleted()) {
			// 删除评价
			userAction = UserActionEnum.DELETE_RATING;
		} else {
			// 新增评价
			userAction = UserActionEnum.RATING;
		}
		LocalDateTime updateTime = activityRatingDetail.getUpdateTime();
		userActionRecordQueue.push(new UserActionRecordQueue.QueueParamDTO(uid, activityId, UserActionTypeEnum.RATING, userAction, String.valueOf(activityRatingDetail.getId()), updateTime));
	}

}