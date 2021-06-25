package com.chaoxing.activity.service.user.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.UserActionRecordMapper;
import com.chaoxing.activity.model.UserActionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**用户行为记录查询服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordQueryService
 * @description
 * @blame wwb
 * @date 2021-06-17 14:07:42
 */
@Slf4j
@Service
public class UserActionRecordQueryService {

	@Resource
	private UserActionRecordMapper userActionRecordMapper;

	/**查询用户在活动下有效的行为记录
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-24 15:16:14
	 * @param uid
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.UserActionRecord>
	*/
	public List<UserActionRecord> listUserValidActionRecord(Integer uid, Integer activityId) {
		return userActionRecordMapper.selectList(new QueryWrapper<UserActionRecord>()
				.lambda()
				.eq(UserActionRecord::getUid, uid)
				.eq(UserActionRecord::getActivityId, activityId)
				.eq(UserActionRecord::getValid, true)
				.orderByAsc(UserActionRecord::getCreateTime)
		);
	}

}
