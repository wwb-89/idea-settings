package com.chaoxing.activity.service.user.result;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.stat.UserResultDTO;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
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

	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;


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

	/**获取用户成绩
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-24 15:07:31
	 * @param uid
	 * @param activityId
	 * @return com.chaoxing.activity.model.UserResult
	*/
	public UserResult getUserResult(Integer uid, Integer activityId) {
		List<UserResult> userResults = userResultMapper.selectList(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getUid, uid)
				.eq(UserResult::getActivityId, activityId)
		);
		return userResults.stream().findFirst().orElse(null);
	}

	/**分页查询活动中用户的成绩
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-24 15:37:06
	 * @param page
	 * @param activityId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.UserResultDTO>
	 */
	public Page<UserResultDTO> pageUserResult(Page<UserResultDTO> page, Integer activityId) {
		// 查找考核配置
//		InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		// 查找已报名的用户id
		page = userResultMapper.pageUserResult(page, activityId);
		return page;
	}

}
