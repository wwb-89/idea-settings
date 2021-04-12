package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.mapper.ActivityClassifyNewMapper;
import com.chaoxing.activity.model.ActivityClassifyNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyNewQueryService
 * @description
 * @blame wwb
 * @date 2021-04-12 09:56:57
 */
@Slf4j
@Service
public class ActivityClassifyNewQueryService {

	@Resource
	private ActivityClassifyNewMapper activityClassifyNewMapper;

	/**分页查询活动类型
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:06:53
	 * @param page
	 * @param activityMarketId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.ActivityClassifyNew>
	*/
	public Page<ActivityClassifyNew> paging(Page<ActivityClassifyNew> page, Integer activityMarketId) {
		return activityClassifyNewMapper.paging(page, activityMarketId);
	}

	/**根据活动市场id和类型名称查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:31:14
	 * @param activityMarketId
	 * @param name
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassifyNew>
	*/
	public List<ActivityClassifyNew> listByMarketIdAndName(Integer activityMarketId, String name) {
		return activityClassifyNewMapper.selectList(new QueryWrapper<ActivityClassifyNew>()
				.lambda()
				.eq(ActivityClassifyNew::getActivityMarketId, activityMarketId)
				.eq(ActivityClassifyNew::getName, name)
				.eq(ActivityClassifyNew::getDeleted, false)
		);
	}

	/**根据id查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:49:23
	 * @param activityClassifyNewId
	 * @return com.chaoxing.activity.model.ActivityClassifyNew
	*/
	public ActivityClassifyNew getById(Integer activityClassifyNewId) {
		return activityClassifyNewMapper.selectOne(new QueryWrapper<ActivityClassifyNew>()
				.lambda()
				.eq(ActivityClassifyNew::getId, activityClassifyNewId)
				.eq(ActivityClassifyNew::getDeleted, false)
		);
	}

	/**根据活动类型id列表查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:01:26
	 * @param activityClassifyNewIds
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassifyNew>
	*/
	public List<ActivityClassifyNew> listByIds(List<Integer> activityClassifyNewIds) {
		return activityClassifyNewMapper.selectList(new QueryWrapper<ActivityClassifyNew>()
				.lambda()
				.in(ActivityClassifyNew::getId, activityClassifyNewIds)
				.eq(ActivityClassifyNew::getDeleted, false)
		);
	}

	/**根据活动市场id查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:28:01
	 * @param activityMarketId
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassifyNew>
	*/
	public List<ActivityClassifyNew> listByActivityMarketId(Integer activityMarketId) {
		return activityClassifyNewMapper.selectList(new QueryWrapper<ActivityClassifyNew>()
			.lambda()
				.eq(ActivityClassifyNew::getActivityMarketId, activityMarketId)
				.eq(ActivityClassifyNew::getDeleted, false)
				.orderByAsc(ActivityClassifyNew::getSequence)
		);

	}

}