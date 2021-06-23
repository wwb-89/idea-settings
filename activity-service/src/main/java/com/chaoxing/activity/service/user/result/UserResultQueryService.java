package com.chaoxing.activity.service.user.result;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.UserResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**用户成绩查询服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultQueryService
 * @description
 * @blame wwb
 * @date 2021-06-23 22:40:49
 */
@Slf4j
@Service
public class UserResultQueryService {

	@Resource
	private UserResultMapper userResultMapper;

	/**用户成绩是否合格
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-23 22:42:19
	 * @param uid
	 * @param activityId
	 * @return boolean
	*/
	public boolean isUserQualified(Integer uid, Integer activityId) {
		List<UserResult> userResults = userResultMapper.selectList(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getUid, uid)
				.eq(UserResult::getActivityId, activityId)
		);
		if (CollectionUtils.isEmpty(userResults)) {
			return false;
		}
		UserResult userResult = userResults.get(0);
		Integer qualifiedStatus = userResult.getQualifiedStatus();
		return Objects.equals(UserResult.QualifiedStatusEnum.QUALIFIED.getValue(), qualifiedStatus);

	}

}
