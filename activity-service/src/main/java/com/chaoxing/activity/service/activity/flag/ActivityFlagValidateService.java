package com.chaoxing.activity.service.activity.flag;

import com.chaoxing.activity.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**活动标示验证服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityFlagValidateService
 * @description
 * @blame wwb
 * @date 2021-04-02 16:43:05
 */
@Slf4j
@Service
public class ActivityFlagValidateService {

	/**是不是双选会活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-02 16:44:33
	 * @param activity
	 * @return boolean
	*/
	public boolean isDualSelect(Activity activity) {
		Activity.ActivityFlagEnum activityFlag = Activity.ActivityFlagEnum.fromValue(activity.getActivityFlag());
		return Objects.equals(activityFlag, Activity.ActivityFlagEnum.DUAL_SELECT);
	}

}