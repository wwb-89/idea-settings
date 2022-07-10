package com.chaoxing.activity.service.activity.collection;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.activity.ActivityCollectionDTO;
import com.chaoxing.activity.mapper.ActivityCollectionMapper;
import com.chaoxing.activity.model.ActivityCollection;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityCollectionQueryService
 * @description
 * @blame wwb
 * @date 2021-01-28 20:50:20
 */
@Slf4j
@Service
public class ActivityCollectionQueryService {

	@Resource
	private ActivityCollectionMapper activityCollectionMapper;

	/**查询收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 20:51:44
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.model.ActivityCollection
	*/
	public ActivityCollection get(Integer activityId, Integer uid) {
		return activityCollectionMapper.selectOne(new QueryWrapper<ActivityCollection>()
				.lambda()
				.eq(ActivityCollection::getActivityId, activityId)
				.eq(ActivityCollection::getUid, uid)
				.eq(ActivityCollection::getDeleted, Boolean.FALSE)
		);
	}

	/**查询收藏活动的uid列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-03 14:03:15
	 * @param activityId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listCollectedUid(Integer activityId) {
		List<ActivityCollection> activityCollections = activityCollectionMapper.selectList(new QueryWrapper<ActivityCollection>()
				.lambda()
				.select(ActivityCollection::getUid)
				.eq(ActivityCollection::getActivityId, activityId)
				.eq(ActivityCollection::getDeleted, Boolean.FALSE)
		);
		if (CollectionUtils.isNotEmpty(activityCollections)) {
			return activityCollections.stream().map(ActivityCollection::getUid).collect(Collectors.toList());
		}
		return Lists.newArrayList();
	}
	
	/**根据activityIds，统计对应活动收藏的用户数量
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-03 14:23:40
	* @param activityIds
	* @return java.util.List<com.chaoxing.activity.dto.activity.ActivityCollectionDTO>
	*/
	public List<ActivityCollectionDTO> statCollectedByActivityIds(List<Integer> activityIds) {
		if (CollectionUtils.isEmpty(activityIds)) {
			return Lists.newArrayList();
		}
		return activityCollectionMapper.statCollectedByActivityIds(activityIds);
	}

}