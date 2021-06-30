package com.chaoxing.activity.service.activity.rating;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.ActivityRatingQueryDTO;
import com.chaoxing.activity.mapper.ActivityRatingDetailMapper;
import com.chaoxing.activity.mapper.ActivityRatingMapper;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.model.ActivityRatingDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
	@Resource
	private ActivityRatingDetailMapper activityRatingDetailMapper;

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

	/**查询用户创建的评论
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 21:36:27
	 * @param activityId
	 * @param uid
	 * @return java.util.List<com.chaoxing.activity.model.ActivityRatingDetail>
	*/
	public List<ActivityRatingDetail> listUserCreated(Integer activityId, Integer uid) {
		return activityRatingDetailMapper.listUserCreated(activityId, uid);
	}

	/**根据评价详情id查询评价详情
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 19:18:30
	 * @param activityRatingDetailId
	 * @return com.chaoxing.activity.model.ActivityRatingDetail
	*/
	public ActivityRatingDetail getDetailById(Integer activityRatingDetailId) {
		return activityRatingDetailMapper.selectOne(new QueryWrapper<ActivityRatingDetail>()
			.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetailId)
				.eq(ActivityRatingDetail::getDeleted, Boolean.FALSE)
		);
	}

	/**根据评价详情id及活动id查询评价详情
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-04-13 14:18:30
	 * @param activityRatingDetailId
	 * @return com.chaoxing.activity.model.ActivityRatingDetail
	*/
	public ActivityRatingDetail getDataByActIdDetailId(Integer activityId, Integer activityRatingDetailId) {
		return activityRatingDetailMapper.selectOne(new QueryWrapper<ActivityRatingDetail>()
			.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetailId)
				.eq(ActivityRatingDetail::getActivityId, activityId)
		);
	}

	/**分页查询
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 19:52:47
	 * @param page
	 * @param activityRatingQueryDTO
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.ActivityRatingDetail>
	*/
	public Page<ActivityRatingDetail> paging(Page<ActivityRatingDetail> page, ActivityRatingQueryDTO activityRatingQueryDTO){
		return activityRatingDetailMapper.listByQuery(page, activityRatingQueryDTO);
	}

	/**分页查询待审核的
	 * @Description
	 * @author wwb
	 * @Date 2021-03-23 13:12:13
	 * @param page
 * @param activityId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.ActivityRatingDetail>
	*/
	public Page<ActivityRatingDetail> pagingWaitAudit(Page<ActivityRatingDetail> page, Integer activityId){
		return activityRatingDetailMapper.pagingWaitAudit(page, activityId);
	}

	/**根据活动id和uid列表查询活动评价详情
	 * @Description
	 * @author wwb
	 * @Date 2021-03-15 19:11:04
	 * @param activityId
	 * @param uids
	 * @return java.util.List<com.chaoxing.activity.model.ActivityRatingDetail>
	*/
	public List<ActivityRatingDetail> listDetail(Integer activityId, List<Integer> uids) {
		return activityRatingDetailMapper.selectList(new QueryWrapper<ActivityRatingDetail>()
			.lambda()
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.in(ActivityRatingDetail::getScorerUid, uids)
				.eq(ActivityRatingDetail::getDeleted, Boolean.FALSE)
		);
	}

	/**根据活动id和评价id列表查询评价
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 19:54:50
	 * @param activityId
	 * @param activityRatingDetailIds
	 * @return java.util.List<com.chaoxing.activity.model.ActivityRatingDetail>
	*/
	public List<ActivityRatingDetail> listDetailByDetailIds(Integer activityId, List<Integer> activityRatingDetailIds) {
		if (CollectionUtils.isEmpty(activityRatingDetailIds)) {
			return Lists.newArrayList();
		}
		return activityRatingDetailMapper.selectList(new QueryWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.in(ActivityRatingDetail::getId, activityRatingDetailIds)
				.eq(ActivityRatingDetail::getDeleted, Boolean.FALSE)
		);
	}

	/**根据活动id和评价id列表查询所有评价
	* @Description
	* @author huxiaolong
	* @Date 2021-06-30 10:51:25
	* @param activityId
	* @param ratingDetailIds
	* @return java.util.List<com.chaoxing.activity.model.ActivityRatingDetail>
	*/
    public List<ActivityRatingDetail> listAllDetailByDetailIds(Integer activityId, List<Integer> ratingDetailIds) {
		if (CollectionUtils.isEmpty(ratingDetailIds)) {
			return Lists.newArrayList();
		}
		return activityRatingDetailMapper.selectList(new QueryWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.in(ActivityRatingDetail::getId, ratingDetailIds)
		);
    }
}
