package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**数据处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityHandleService
 * @description
 * @blame wwb
 * @date 2020-11-10 15:52:50
 */
@Slf4j
@Service
public class ActivityHandleService {

	@Resource
	private ActivityMapper activityMapper;

	@Resource
	private ActivityValidationService activityValidationService;

	/**新增活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 15:54:16
	 * @param activity
	 * @param loginUser
	 * @return void
	*/
	private void add(Activity activity, LoginUserDTO loginUser) {
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		// 是否开启参与设置
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {

		}
		// 模块

		// 网页

	}

}