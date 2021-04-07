package com.chaoxing.activity.service.activity.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityManagerMapper;
import com.chaoxing.activity.model.ActivityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**组织者查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagerQueryService
 * @description
 * @blame wwb
 * @date 2021-03-29 09:43:46
 */
@Slf4j
@Service
public class ActivityManagerQueryService {

	@Resource
	private ActivityManagerMapper activityManagerMapper;

	/**根据活动id查询组织者列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 09:45:19
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.ActivityManager>
	*/
	public List<ActivityManager> listByActivityId(Integer activityId) {
		return activityManagerMapper.selectList(new QueryWrapper<ActivityManager>()
			.lambda()
				.eq(ActivityManager::getActivityId, activityId)
		);
	}

	/**根据活动id列表查询组织者列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-06 14:07:58
	 * @param activityIds
	 * @return java.util.List<com.chaoxing.activity.model.ActivityManager>
	*/
	public List<ActivityManager> listByActivityId(List<Integer> activityIds) {
		return activityManagerMapper.selectList(new QueryWrapper<ActivityManager>()
				.lambda()
				.in(ActivityManager::getActivityId, activityIds)
		);
	}

}