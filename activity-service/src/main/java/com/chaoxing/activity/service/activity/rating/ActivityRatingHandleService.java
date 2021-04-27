package com.chaoxing.activity.service.activity.rating;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityRatingDetailMapper;
import com.chaoxing.activity.mapper.ActivityRatingMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.event.UserRatingChangeEventService;
import com.chaoxing.activity.util.CalculateUtils;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private UserRatingChangeEventService userRatingChangeEventService;

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
	public void addRating(ActivityRatingDetail activityRatingDetail, LoginUserDTO loginUser) {
		Integer operateUid = loginUser.getUid();
		Integer activityId = activityRatingDetail.getActivityId();
		Activity activity = activityValidationService.activityExist(activityId);
		// 验证是否可以评价
		activityRatingValidateService.canSubmitRating(activityId, operateUid);
		activityRatingDetail.setScorerUid(loginUser.getUid());
		activityRatingDetail.setScorerUserName(loginUser.getRealName());
		Boolean anonymous = activityRatingDetail.getAnonymous();
		anonymous = Optional.ofNullable(anonymous).orElse(Boolean.FALSE);
		activityRatingDetail.setAnonymous(anonymous);
		activityRatingDetail.setCreateUid(operateUid);
		// 是不是管理员创建的评价？是则直接审核通过
		boolean activityManager = activityValidationService.isManageAble(activity, operateUid);
		if(activity.getRatingNeedAudit() && !activityManager){
			activityRatingDetail.setAuditStatus(ActivityRatingDetail.AuditStatus.WAIT.getValue());
		}else{
			activityRatingDetail.setAuditStatus(ActivityRatingDetail.AuditStatus.PASSED.getValue());
		}
		activityRatingDetailMapper.insert(activityRatingDetail);
		if (activityRatingValidateService.isNeedUpdateActivityScore(activityRatingDetail)) {
			updateActivityScore(activityRatingDetail.getActivityId(), 1, activityRatingDetail.getScore());
		}
		// 评价变更
		userRatingChangeEventService.change(loginUser.getUid(), activity.getSignId());
	}

	/**更新活动评分
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 18:18:11
	 * @param activityId 活动id
	 * @param scoreNum 评分人数 可以为负数
	 * @param totalScore 总分 可以为负数
	 * @return void
	*/
	private void updateActivityScore(Integer activityId, Integer scoreNum, BigDecimal totalScore) {
		// 获取锁
		String lockKey = getActivityRatingLockKey(activityId);
		distributedLock.lock(lockKey, CacheConstant.LOCK_MAXIMUM_WAIT_TIME, () -> {
			ActivityRating activityRating = activityRatingQueryService.getByActivityId(activityId);
			if (activityRating == null) {
				// 初始化一个
				activityRating = ActivityRating.getDefault(activityId);
			}
			activityRating.setScoreNum(activityRating.getScoreNum() + scoreNum);
			activityRating.setTotalScore(activityRating.getTotalScore().add(totalScore));
			// 计算得分
			double score;
			if (activityRating.getScoreNum().compareTo(0) < 1) {
				score = 0d;
			} else {
				score = CalculateUtils.div(activityRating.getTotalScore().doubleValue(), activityRating.getScoreNum());
			}
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
		}, e -> {
			log.error("更新活动:{}评分error:{}", activityId, e.getMessage());
			throw new BusinessException("更新活动评分失败");
		});
	}

	/**更新评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 17:56:40
	 * @param activityRatingDetail
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void updateRating(ActivityRatingDetail activityRatingDetail, LoginUserDTO loginUser) {
		ActivityRatingDetail existActivityRatingDetail = activityRatingValidateService.editAble(activityRatingDetail.getId(), loginUser.getUid());
		Integer activityId = existActivityRatingDetail.getActivityId();
		Activity activity = activityValidationService.activityExist(activityId);
		Boolean ratingNeedAudit = activity.getRatingNeedAudit();
		ratingNeedAudit = Optional.ofNullable(ratingNeedAudit).orElse(Boolean.FALSE);
		Boolean anonymous = activityRatingDetail.getAnonymous();
		anonymous = Optional.ofNullable(anonymous).orElse(Boolean.FALSE);
		ActivityRatingDetail.AuditStatus auditStaus = ratingNeedAudit ? ActivityRatingDetail.AuditStatus.WAIT : ActivityRatingDetail.AuditStatus.PASSED;
		activityRatingDetailMapper.update(null, new UpdateWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetail.getId())
				.set(ActivityRatingDetail::getScore, activityRatingDetail.getScore())
				.set(ActivityRatingDetail::getComment, activityRatingDetail.getComment())
				.set(ActivityRatingDetail::getAnonymous, anonymous)
				.set(ActivityRatingDetail::getAuditStatus, auditStaus.getValue())
		);
		// 更新评价记分规则：原来是通过的则需要减分， 然后看现在是不是通过的？如果是则加分
		int scoreNum = 0;
		BigDecimal totalScore = BigDecimal.valueOf(0);
		if (activityRatingValidateService.isNeedUpdateActivityScore(existActivityRatingDetail)) {
			scoreNum = -1;
			totalScore = totalScore.subtract(existActivityRatingDetail.getScore());
		}
		if (Objects.equals(ActivityRatingDetail.AuditStatus.PASSED, auditStaus)) {
			scoreNum += 1;
			totalScore = totalScore.add(activityRatingDetail.getScore());
		}
		updateActivityScore(activityId, scoreNum, totalScore);
	}

	/**删除评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 17:57:54
	 * @param ratingId
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void deleteRating(Integer ratingId, LoginUserDTO loginUser) {
		ActivityRatingDetail existActivityRatingDetail = activityRatingValidateService.editAble(ratingId, loginUser.getUid());
		activityRatingDetailMapper.update(null, new UpdateWrapper<ActivityRatingDetail>()
			.lambda()
				.eq(ActivityRatingDetail::getId, ratingId)
				.set(ActivityRatingDetail::getDeleted, Boolean.TRUE)
		);
		Integer activityId = existActivityRatingDetail.getActivityId();
		if (activityRatingValidateService.isNeedUpdateActivityScore(existActivityRatingDetail)) {
			updateActivityScore(activityId, -1, BigDecimal.valueOf(0).subtract(existActivityRatingDetail.getScore()));
		}
		// 评价成功后的额外操作
		Activity activity = activityValidationService.activityExist(activityId);
		// 评价变更
		userRatingChangeEventService.change(loginUser.getUid(), activity.getSignId());
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

	/**评价通过
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:39:11
	 * @param loginUser
	 * @param activityId
	 * @param activityRatingDetailId
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void pass(LoginUserDTO loginUser, Integer activityId, Integer activityRatingDetailId){
		activityValidationService.manageAble(activityId, loginUser.getUid());
		ActivityRatingDetail activityRatingDetail = activityRatingValidateService.auditAble(activityRatingDetailId);
		activityRatingDetailMapper.update(null, new UpdateWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetailId)
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.set(ActivityRatingDetail::getAuditStatus, ActivityRatingDetail.AuditStatus.PASSED.getValue())
		);
		updateActivityScore(activityId, 1, activityRatingDetail.getScore());
	}

	/**评价不通过
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:50:55
	 * @param loginUser
	 * @param activityId
	 * @param activityRatingDetailId
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void reject(LoginUserDTO loginUser, Integer activityId, Integer activityRatingDetailId){
		activityValidationService.manageAble(activityId, loginUser.getUid());
		activityRatingValidateService.auditAble(activityRatingDetailId);
		activityRatingDetailMapper.update(null, new UpdateWrapper<ActivityRatingDetail>()
				.lambda()
				.eq(ActivityRatingDetail::getId, activityRatingDetailId)
				.eq(ActivityRatingDetail::getActivityId, activityId)
				.set(ActivityRatingDetail::getAuditStatus, ActivityRatingDetail.AuditStatus.REJECT.getValue())
		);
	}

	/**批量通过评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:51:16
	 * @param activityId
	 * @param ratingDetailIds
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void batchPass(Integer activityId, List<Integer> ratingDetailIds, LoginUserDTO loginUser){
		batchAuditRating(activityId, ratingDetailIds, loginUser, ActivityRatingDetail.AuditStatus.PASSED);
	}

	/**批量不通过评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 20:00:09
	 * @param activityId
	 * @param ratingDetailIds
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void batchReject(Integer activityId, List<Integer> ratingDetailIds, LoginUserDTO loginUser){
		batchAuditRating(activityId, ratingDetailIds, loginUser, ActivityRatingDetail.AuditStatus.REJECT);
	}

	/**批量审核评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 20:02:20
	 * @param activityId
	 * @param ratingDetailIds
	 * @param loginUser
	 * @param auditStatus
	 * @return void
	*/
	private void batchAuditRating(Integer activityId, List<Integer> ratingDetailIds, LoginUserDTO loginUser, ActivityRatingDetail.AuditStatus auditStatus) {
		activityValidationService.manageAble(activityId, loginUser.getUid());
		List<ActivityRatingDetail> activityRatingDetails = activityRatingQueryService.listDetailByDetailIds(activityId, ratingDetailIds);
		if (CollectionUtils.isNotEmpty(activityRatingDetails)) {
			List<ActivityRatingDetail> auditAbleActivityRatingDetails = Lists.newArrayList();
			BigDecimal totalScore = BigDecimal.valueOf(0);
			for (ActivityRatingDetail activityRatingDetail : activityRatingDetails) {
				boolean auditAble = activityRatingValidateService.isAuditAble(activityRatingDetail);
				if (auditAble) {
					auditAbleActivityRatingDetails.add(activityRatingDetail);
					totalScore = totalScore.add(activityRatingDetail.getScore());
				}
			}
			if (CollectionUtils.isNotEmpty(auditAbleActivityRatingDetails)) {
				activityRatingDetailMapper.batchUpAuditStatus(activityId, auditAbleActivityRatingDetails.stream().map(ActivityRatingDetail::getId).collect(Collectors.toList()), auditStatus.getValue());
				if (Objects.equals(ActivityRatingDetail.AuditStatus.PASSED, auditStatus)) {
					updateActivityScore(activityId, auditAbleActivityRatingDetails.size(), totalScore);
				}
			}
		}
	}

}