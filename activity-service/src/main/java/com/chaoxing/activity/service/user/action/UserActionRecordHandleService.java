package com.chaoxing.activity.service.user.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.UserActionRecordMapper;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.UserActionRecord;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserResultQueueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**用户行为记录处理服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordHandleService
 * @description
 * @blame wwb
 * @date 2021-06-17 14:07:54
 */
@Slf4j
@Service
public class UserActionRecordHandleService {

	@Resource
	private UserActionRecordMapper userActionRecordMapper;
	@Resource
	private UserResultQueueService userResultQueueService;

	/**新增用户行为记录
	 * @Description 记录完用户行为后需要通知计算用户的成绩（中的得分信息）
	 * @author wwb
	 * @Date 2021-06-17 16:38:24
	 * @param queueParam
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void addUserActionRecord(UserActionQueueService.QueueParamDTO queueParam) {
		Integer uid = queueParam.getUid();
		Integer activityId = queueParam.getActivityId();
		UserActionRecord userActionRecord = UserActionRecord.builder()
				.uid(uid)
				.activityId(activityId)
				.actionType(queueParam.getUserActionType().getValue())
				.action(queueParam.getUserAction().getValue())
				.actionIdentify(queueParam.getIdentify())
				.actionDescription(queueParam.getUserAction().getName())
				.build();
		userActionRecordMapper.insert(userActionRecord);
		userResultQueueService.push(new UserResultQueueService.QueueParamDTO(uid, activityId));
	}

	/**使用户行为记录有效
	 * @Description
	 * @author wwb
	 * @Date 2021-06-24 10:46:47
	 * @param activityId
	 * @param identify
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void enableUserActionRecord(Integer activityId, String identify) {
		updateUserRecordValid(activityId, identify, true);
	}

	/**使用户行为记录无效
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-24 10:46:47
	 * @param activityId
	 * @param identify
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void disableUserActionRecord(Integer activityId, String identify) {
		updateUserRecordValid(activityId, identify, false);
	}

	/**更新用户行为记录有效状态
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-24 10:52:10
	 * @param activityId
	 * @param identify
	 * @param valid
	 * @return void
	*/
	private void updateUserRecordValid(Integer activityId, String identify, boolean valid) {
		List<UserActionRecord> userActionRecords = userActionRecordMapper.selectList(new QueryWrapper<UserActionRecord>()
				.lambda()
				.eq(UserActionRecord::getActivityId, activityId)
				.eq(UserActionRecord::getActionIdentify, identify)
				.eq(UserActionRecord::getValid, !valid)
		);
		if (CollectionUtils.isEmpty(userActionRecords)) {
			return;
		}
		int affectiveCount = userActionRecordMapper.update(null, new UpdateWrapper<UserActionRecord>()
				.lambda()
				.eq(UserActionRecord::getActivityId, activityId)
				.eq(UserActionRecord::getActionIdentify, identify)
				.set(UserActionRecord::getValid, valid)
		);
		if (affectiveCount > 0) {
			for (UserActionRecord userActionRecord : userActionRecords) {
				userResultQueueService.push(new UserResultQueueService.QueueParamDTO(userActionRecord.getUid(), activityId));
			}
		}
	}

}
