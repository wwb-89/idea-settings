package com.chaoxing.activity.service.activity.scope;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.mapper.ActivityScopeMapper;
import com.chaoxing.activity.model.ActivityScope;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动发布范围查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityScopeQueryService
 * @description
 * @blame wwb
 * @date 2020-12-20 16:25:36
 */
@Slf4j
@Service
public class ActivityScopeQueryService {

	@Resource
	private ActivityScopeMapper activityScopeMapper;

	/**根据活动id查询层级架构列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-20 16:35:12
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO>
	*/
	public List<WfwAreaDTO> listByActivityId(Integer activityId) {
		List<WfwAreaDTO> result;
		List<ActivityScope> activityScopes = activityScopeMapper.selectList(new QueryWrapper<ActivityScope>()
				.lambda()
				.eq(ActivityScope::getActivityId, activityId)
		);
		if (CollectionUtils.isNotEmpty(activityScopes)) {
			result = ActivityScope.convert2WfwRegionalArchitectures(activityScopes);
		} else {
			result = Lists.newArrayList();
		}
		return result;
	}

	/**查询机构参与的报名签到id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-01-13 17:52:03
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listOrgParticipateSignId(List<Integer> fids) {
		return activityScopeMapper.listOrgParticipateSignId(fids);
	}

}