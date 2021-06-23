package com.chaoxing.activity.service.user.action;

import com.chaoxing.activity.mapper.UserActionRecordMapper;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserResultQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户行为处理服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionHandleService
 * @description
 * @blame wwb
 * @date 2021-06-17 14:07:54
 */
@Slf4j
@Service
public class UserActionHandleService {

	@Resource
	private UserResultMapper userResultMapper;
	@Resource
	private UserActionRecordMapper userActionMapper;

	@Resource
	private UserActionQueryService userActionQueryService;
	@Resource
	private UserResultQueueService userResultQueueService;

	/**更新用户行为
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-17 16:38:24
	 * @param queueParam
	 * @return void
	*/
	public void updateUserAction(UserActionQueueService.QueueParamDTO queueParam) {
		Integer uid = queueParam.getUid();
		Integer activityId = queueParam.getActivityId();

		userResultQueueService.push(UserResultQueueService.QueueParamDTO.builder()
				.uid(uid)
				.activityId(activityId)
				.build());
	}

}
