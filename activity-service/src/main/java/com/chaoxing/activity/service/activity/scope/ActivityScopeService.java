package com.chaoxing.activity.service.activity.scope;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityScopeMapper;
import com.chaoxing.activity.model.ActivityScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动参与范围服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityScopeService
 * @description
 * @blame wwb
 * @date 2020-11-12 16:07:17
 */
@Slf4j
@Service
public class ActivityScopeService {

	@Resource
	private ActivityScopeMapper activityScopeMapper;

	/**根据活动id删除参与范围
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 16:12:00
	 * @param activityId
	 * @return void
	*/
	public void deleteByActivityId(Integer activityId) {
		activityScopeMapper.delete(new QueryWrapper<ActivityScope>()
			.lambda()
				.eq(ActivityScope::getActivityId, activityId)
		);
	}

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 16:16:17
	 * @param activityScopes
	 * @return void
	*/
	public void batchAdd(List<ActivityScope> activityScopes) {
		activityScopeMapper.batchAdd(activityScopes);
	}

}