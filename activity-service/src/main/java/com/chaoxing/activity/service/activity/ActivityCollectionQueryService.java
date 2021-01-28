package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityCollectionMapper;
import com.chaoxing.activity.model.ActivityCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

}