package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**活动验证服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityValidationService
 * @description
 * @blame wwb
 * @date 2020-11-10 15:57:22
 */
@Slf4j
@Service
public class ActivityValidationService {

	/**新增活动输入验证
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 16:22:48
	 * @param activity
	 * @return void
	*/
	public void addInputValidate(Activity activity) {
		String name = activity.getName();
		if (StringUtils.isEmpty(name)) {
			throw new BusinessException("活动名称不能为空");
		}
		Date startDate = activity.getStartDate();
		if (startDate == null) {
			throw new BusinessException("活动开始时间不能为空");
		}
		Date endDate = activity.getEndDate();
		if (endDate == null) {
			throw new BusinessException("活动结束时间不能为空");
		}
		if (startDate.after(endDate)) {
			throw new BusinessException("活动开始时间不能晚于结束时间");
		}
		String coverCloudId = activity.getCoverCloudId();
		if (StringUtils.isEmpty(coverCloudId)) {
			throw new BusinessException("活动封面不能为空");
		}
		String address = activity.getAddress();
		if (StringUtils.isEmpty(address)) {
			throw new BusinessException("活动位置不能为空");
		}
		Integer activityClassifyId = activity.getActivityClassifyId();
		if (activityClassifyId == null) {
			throw new BusinessException("活动分类不能为空");
		}
	}

}