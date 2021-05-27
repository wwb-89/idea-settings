package com.chaoxing.activity.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityRatingDetailMapper;
import com.chaoxing.activity.model.ActivityRatingDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户统计服务
 * @author wwb
 * @version ver 1.0
 * @className UserStatService
 * @description
 * @blame wwb
 * @date 2021-05-27 23:04:08
 */
@Slf4j
@Service
public class UserStatService {

	@Resource
	private ActivityRatingDetailMapper activityRatingDetailMapper;

	/**统计用户评价的次数
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-27 23:05:12
	 * @param uid
	 * @return java.lang.Integer
	*/
	public Integer countUserRatingNum(Integer uid) {
		return activityRatingDetailMapper.selectCount(new QueryWrapper<ActivityRatingDetail>()
			.lambda()
				.eq(ActivityRatingDetail::getScorerUid, uid)
				.eq(ActivityRatingDetail::getDeleted, false)
		);
	}

}
