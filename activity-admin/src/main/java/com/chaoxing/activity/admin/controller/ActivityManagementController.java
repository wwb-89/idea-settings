package com.chaoxing.activity.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**活动管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-12-08 19:06:17
 */
@Slf4j
@Controller
@RequestMapping("activity")
public class ActivityManagementController {

	/**活动主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-08 19:07:11
	 * @param
	 * @return java.lang.String
	*/
	@RequestMapping("{activityId}")
	public String index(@PathVariable Integer activityId) {
		return "pc/activity-index";
	}

}
