package com.chaoxing.activity.service.activity.rating;

import com.chaoxing.activity.dto.manager.sign.SignUp;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
		SignUp signUp = signApiService.getBySignId(signId);
		if (signUp == null) {
			// 没有开启报名
			return;
		}
		// 用户是否报名
		boolean signedUpSuccess = signApiService.isSignedUpSuccess(signUp.getId(), uid);
		if (!signedUpSuccess) {
			throw new BusinessException("未报名");
		}
	}

}