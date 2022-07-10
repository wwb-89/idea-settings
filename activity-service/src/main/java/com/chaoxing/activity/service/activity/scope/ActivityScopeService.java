package com.chaoxing.activity.service.activity.scope;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.mapper.ActivityScopeMapper;
import com.chaoxing.activity.model.ActivityScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**活动发布范围服务
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

	/**根据活动id删除发布范围
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
	 * @param activityId
	 * @param wfwRegionalArchitectureDtos
	 * @return void
	*/
	public void batchAdd(Integer activityId, List<WfwAreaDTO> wfwRegionalArchitectureDtos) {
		deleteByActivityId(activityId);
		List<ActivityScope> activityScopes = WfwAreaDTO.buildActivityScopes(wfwRegionalArchitectureDtos);
		if (CollectionUtils.isNotEmpty(activityScopes)) {
			activityScopes.forEach(v -> v.setActivityId(activityId));
			activityScopeMapper.batchAdd(activityScopes);
		}
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-02-28 11:56:30
	 * @param activityId
	 * @return
	 */
	public List<ActivityScope> listByActivity(Integer activityId) {
		return activityScopeMapper.selectList(new LambdaQueryWrapper<ActivityScope>().eq(ActivityScope::getActivityId, activityId));
	}

}