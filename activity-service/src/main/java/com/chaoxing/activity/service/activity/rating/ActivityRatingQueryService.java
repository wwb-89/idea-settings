package com.chaoxing.activity.service.activity.rating;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityRatingMapper;
import com.chaoxing.activity.model.ActivityRating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动评价查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityRatingQueryService
 * @description
 * @blame wwb
 * @date 2021-03-08 16:45:11
 */
@Slf4j
@Service
public class ActivityRatingQueryService {

	@Resource
	private ActivityRatingMapper activityRatingMapper;

	/**根据活动id查询活动评价信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 19:54:37
	 * @param activityId
	 * @return com.chaoxing.activity.model.ActivityRating
	*/
	public ActivityRating getByActivityId(Integer activityId) {
		return activityRatingMapper.selectOne(new QueryWrapper<ActivityRating>()
			.lambda()
				.eq(ActivityRating::getActivityId, activityId)
		);
	}

}
