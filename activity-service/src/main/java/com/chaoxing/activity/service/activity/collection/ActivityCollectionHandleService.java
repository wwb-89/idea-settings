package com.chaoxing.activity.service.activity.collection;

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

	@Resource
	private ActivityCollectionValidateService activityCollectionValidateService;

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
		collect(activityId, uid);
	}

	/**收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 20:44:52
	 * @param activityId
	 * @param uid
	 * @return void
	*/
	public void collect(Integer activityId, Integer uid) {
		if (!activityCollectionValidateService.isCollected(activityId, uid)) {
			ActivityCollection activityCollection = ActivityCollection.builder()
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
		cancelCollect(activityId, loginUser.getUid());
	}

	/**取消收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 20:45:28
	 * @param activityId
	 * @param uid
	 * @return void
	*/
	public void cancelCollect(Integer activityId, Integer uid) {
		activityCollectionMapper.update(null, new UpdateWrapper<ActivityCollection>()
				.lambda()
				.eq(ActivityCollection::getActivityId, activityId)
				.eq(ActivityCollection::getUid, uid)
				.set(ActivityCollection::getDeleted, Boolean.TRUE)
		);
	}

}