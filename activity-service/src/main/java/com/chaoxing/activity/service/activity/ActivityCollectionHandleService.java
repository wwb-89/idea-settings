package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityCollectionMapper;
import com.chaoxing.activity.model.ActivityCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动收藏服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCollectionHandleService
 * @description
 * @blame wwb
 * @date 2021-01-28 15:32:28
 */
@Slf4j
@Service
public class ActivityCollectionHandleService {

	@Resource
	private ActivityCollectionMapper activityCollectionMapper;

	/**收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 15:39:06
	 * @param activityId
	 * @param loginUser
	 * @return void
	*/
	public void collect(Integer activityId, LoginUserDTO loginUser) {
		Integer uid = loginUser.getUid();
		ActivityCollection activityCollection = activityCollectionMapper.selectOne(new QueryWrapper<ActivityCollection>()
				.lambda()
				.eq(ActivityCollection::getActivityId, activityId)
				.eq(ActivityCollection::getUid, uid)
				.eq(ActivityCollection::getDeleted, Boolean.FALSE)
		);
		if (activityCollection == null) {
			activityCollection = ActivityCollection.builder()
					.activityId(activityId)
					.uid(uid)
					.build();
			activityCollectionMapper.insert(activityCollection);
		}
	}

	/**取消收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 15:35:17
	 * @param activityId
	 * @param loginUser
	 * @return void
	*/
	public void cancelCollect(Integer activityId, LoginUserDTO loginUser) {
		activityCollectionMapper.update(null, new UpdateWrapper<ActivityCollection>()
			.lambda()
				.eq(ActivityCollection::getActivityId, activityId)
				.eq(ActivityCollection::getUid, loginUser.getUid())
				.set(ActivityCollection::getDeleted, Boolean.TRUE)
		);
	}

}