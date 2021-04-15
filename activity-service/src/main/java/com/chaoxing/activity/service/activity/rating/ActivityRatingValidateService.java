package com.chaoxing.activity.service.activity.rating;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**活动评价验证服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityRatingValidateService
 * @description
 * @blame wwb
 * @date 2021-03-08 16:45:40
 */
@Slf4j
@Service
public class ActivityRatingValidateService {

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityRatingQueryService activityRatingQueryService;

	/**能提交评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 18:43:30
	 * @param activityId
	 * @param uid
	 * @return void
	*/
	public void canSubmitRating(Integer activityId, Integer uid) {
		Activity activity = activityValidationService.activityExist(activityId);
		// 是否有开启评价
		Boolean openRating = activity.getOpenRating();
		openRating = Optional.ofNullable(openRating).orElse(Boolean.FALSE);
		if (!openRating) {
			throw new BusinessException("活动未开启评价功能");
		}
		Integer signId = activity.getSignId();
		if (signId == null) {
			// 没有开启报名签到
			return;
		}
		// 报名签到是否开启了报名
		boolean openSignUp = signApiService.isOpenSignUp(signId);
		if (!openSignUp) {
			// 没有开启报名
			return;
		}
		// 用户是否报名
		boolean signedUpSuccess = signApiService.isSignedUp(signId, uid);
		if (!signedUpSuccess) {
			throw new BusinessException("未报名");
		}
	}

	/**是否可以提交评价
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:27:39
	 * @param activityId
	 * @param uid
	 * @return java.lang.Boolean
	*/
	public Boolean submitRatingAble(Integer activityId, Integer uid){
		try {
			canSubmitRating(activityId, uid);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**评价详情存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:45:28
	 * @param activityRatingDetailId
	 * @return com.chaoxing.activity.model.ActivityRatingDetail
	*/
	public ActivityRatingDetail detailExist(Integer activityRatingDetailId) {
		ActivityRatingDetail existActivityRatingDetail = activityRatingQueryService.getDetailById(activityRatingDetailId);
		Optional.ofNullable(existActivityRatingDetail).orElseThrow(() -> new BusinessException("评价不存在"));
		return existActivityRatingDetail;
	}

	/**可编辑的
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:28:41
	 * @param activityRatingDetailId
	 * @param uid
	 * @return com.chaoxing.activity.model.ActivityRatingDetail
	*/
	public ActivityRatingDetail editAble(Integer activityRatingDetailId, Integer uid) {
		ActivityRatingDetail activityRatingDetail = detailExist(activityRatingDetailId);
		if (!Objects.equals(activityRatingDetail.getCreateUid(), uid)) {
			throw new BusinessException("无权限");
		}
		return activityRatingDetail;
	}

	/**是否需要更新活动的评分
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:36:01
	 * @param activityRatingDetail
	 * @return boolean
	*/
	public boolean isNeedUpdateActivityScore(ActivityRatingDetail activityRatingDetail) {
		Integer auditStatus = activityRatingDetail.getAuditStatus();
		ActivityRatingDetail.AuditStatus auditStatusEnum = ActivityRatingDetail.AuditStatus.fromValue(auditStatus);
		if (Objects.equals(ActivityRatingDetail.AuditStatus.PASSED, auditStatusEnum)) {
			return true;
		}
		return false;
	}

	/**可被审核
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:48:12
	 * @param activityRatingDetailId
	 * @return com.chaoxing.activity.model.ActivityRatingDetail
	*/
	public ActivityRatingDetail auditAble(Integer activityRatingDetailId) {
		ActivityRatingDetail activityRatingDetail = detailExist(activityRatingDetailId);
		Integer auditStatus = activityRatingDetail.getAuditStatus();
		ActivityRatingDetail.AuditStatus auditStatusEnum = ActivityRatingDetail.AuditStatus.fromValue(auditStatus);
		if (!Objects.equals(ActivityRatingDetail.AuditStatus.WAIT, auditStatusEnum)) {
			throw new BusinessException("评价已被审核");
		}
		return activityRatingDetail;
	}

	/**是否能被审核
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 19:57:28
	 * @param activityRatingDetail
	 * @return boolean
	*/
	public boolean isAuditAble(ActivityRatingDetail activityRatingDetail) {
		Integer auditStatus = activityRatingDetail.getAuditStatus();
		ActivityRatingDetail.AuditStatus auditStatusEnum = ActivityRatingDetail.AuditStatus.fromValue(auditStatus);
		if (!Objects.equals(ActivityRatingDetail.AuditStatus.WAIT, auditStatusEnum)) {
			return false;
		}
		return true;
	}

}