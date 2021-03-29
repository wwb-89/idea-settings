package com.chaoxing.activity.service.activity.manager;

import com.chaoxing.activity.model.ActivityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**组织者验证服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagerValidationService
 * @description
 * @blame wwb
 * @date 2021-03-29 09:43:12
 */
@Slf4j
@Service
public class ActivityManagerValidationService {

	@Resource
	private ActivityManagerQueryService activityManagerQueryService;

	/**是不是组织者
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 09:48:31
	 * @param activityId
	 * @param uid
	 * @return boolean
	*/
	public boolean isManager(Integer activityId, Integer uid) {
		List<ActivityManager> activityManagers = activityManagerQueryService.listByActivityId(activityId);
		for (ActivityManager activityManager : activityManagers) {
			if (Objects.equals(activityManager.getUid(), uid)) {
				return true;
			}
		}
		return false;
	}

}