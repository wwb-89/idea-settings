package com.chaoxing.activity.service.activity.rating;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityRatingDetailMapper;
import com.chaoxing.activity.mapper.ActivityRatingMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.util.CalculateUtils;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

/**活动评价处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityRatingHandleService
 * @description
 * @blame wwb
 * @date 2021-03-08 16:44:42
 */
@Slf4j
@Service
public class ActivityRatingHandleService {

	@Resource
	private ActivityRatingMapper activityRatingMapper;
	@Resource
	private ActivityRatingDetailMapper activityRatingDetailMapper;

	@Resource
	private ActivityRatingValidateService activityRatingValidateService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityRatingQueryService activityRatingQueryService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private DistributedLock distributedLock;

	/**新增评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 17:00:35
	 * @param activityRatingDetail
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(ActivityRatingDetail activityRatingDetail, LoginUserDTO loginUser) {
		activityRatingDetail.setScorerUid(loginUser.getUid());
		activityRatingDetail.setScorerUserName(loginUser.getRealName());
		activityRatingDetail.setCreateUid(loginUser.getUid());
		Integer activityId = activityRatingDetail.getActivityId();
		// 获取锁
		String lockKey = getActivityRatingLockKey(activityId);
		Consumer<Exception> fail = (e) -> {
			log.error("新增活动评价:{}, error:{}", JSON.toJSONString(activityRatingDetail), e.getMessage());
			throw new BusinessException("新增评价失败");
		};
		distributedLock.lock(lockKey, CacheConstant.LOCK_MAXIMUM_WAIT_TIME, () -> {
			// 获取锁成功
			// 验证是否可以评价
			activityRatingValidateService.canSubmitRating(activityId, loginUser.getUid());
			Activity activity = activityValidationService.activityExist(activityId);
			if(activity.getRatingNeedAudit()){
				activityRatingDetail.setAuditStatus(ActivityRatingDetail.AuditStatus.WAIT.getValue());
			}else{
				activityRatingDetail.setAuditStatus(ActivityRatingDetail.AuditStatus.PASSED.getValue());
			}
			activityRatingDetailMapper.insert(activityRatingDetail);
			ActivityRating activityRating = activityRatingQueryService.getByActivityId(activityId);
			if (activityRating == null) {
				// 初始化一个
				activityRating = ActivityRating.getDefault(activityId);
			}
			activityRating.setScoreNum(activityRating.getScoreNum() + 1);
			activityRating.setTotalScore(activityRating.getTotalScore().add(activityRatingDetail.getScore()));
			// 计算得分
			double score = CalculateUtils.div(activityRating.getTotalScore().doubleValue(), activityRating.getScoreNum());
			activityRating.setScore(new BigDecimal(score));
			if (activityRating.getId() == null) {
				// 新增
				activityRatingMapper.insert(activityRating);
			} else {
				// 更新
				activityRatingMapper.update(null, new UpdateWrapper<ActivityRating>()
					.lambda()
						.eq(ActivityRating::getId, activityRating.getId())
						.set(ActivityRating::getScore, activityRating.getScore())
						.set(ActivityRating::getScoreNum, activityRating.getScoreNum())
						.set(ActivityRating::getTotalScore, activityRating.getTotalScore())
				);
			}
			return null;
		}, (e) -> fail.accept(e));
	}

	/**获取活动评价缓存lock key
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 19:34:33
	 * @param activityId
	 * @return java.lang.String
	*/
	private String getActivityRatingLockKey(Integer activityId) {
		return CacheConstant.LOCK_CACHE_KEY_PREFIX + "activity" + CacheConstant.CACHE_KEY_SEPARATOR + activityId + CacheConstant.CACHE_KEY_SEPARATOR + "rating";
	}

	/**
	 * 通过
	 * @param loginUser
	 * @param activityId
	 * @param activityRatingDetailId
	 */
	public void pass(LoginUserDTO loginUser, Integer activityId, Integer activityRatingDetailId){
		activityValidationService.manageAble(activityId, loginUser, null);
		activityRatingDetailMapper.update(null, new UpdateWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetailId)
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.set(ActivityRatingDetail::getAuditStatus, ActivityRatingDetail.AuditStatus.PASSED.getValue())
		);
	}

	/**
	 * 不通过
	 * @param loginUser
	 * @param activityId
	 * @param activityRatingDetailId
	 */
	public void reject(LoginUserDTO loginUser, Integer activityId, Integer activityRatingDetailId){
		activityValidationService.manageAble(activityId, loginUser, null);
		activityRatingDetailMapper.update(null, new UpdateWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetailId)
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.set(ActivityRatingDetail::getAuditStatus, ActivityRatingDetail.AuditStatus.REJECT.getValue())
		);
	}

	/**
	 * 批量通过
	 * @param loginUser
	 * @param activityId
	 * @param ratingDetailIds
	 */
	public void batchPass(LoginUserDTO loginUser, Integer activityId, List<Integer> ratingDetailIds){
		activityValidationService.manageAble(activityId, loginUser, null);
		activityRatingDetailMapper.batchUpAuditStatus(activityId, ratingDetailIds, ActivityRatingDetail.AuditStatus.PASSED.getValue());
	}

	/**
	 * 批量不通过
	 * @param loginUser
	 * @param activityId
	 * @param ratingDetailIds
	 */
	public void batchReject(LoginUserDTO loginUser, Integer activityId, List<Integer> ratingDetailIds){
		activityValidationService.manageAble(activityId, loginUser, null);
		activityRatingDetailMapper.batchUpAuditStatus(activityId, ratingDetailIds, ActivityRatingDetail.AuditStatus.REJECT.getValue());
	}
}
