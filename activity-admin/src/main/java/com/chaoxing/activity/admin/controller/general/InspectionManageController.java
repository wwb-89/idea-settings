package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.util.UserAgentUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**考核管理
 * @author wwb
 * @version ver 1.0
 * @className InspectionManageController
 * @description
 * @blame wwb
 * @date 2021-06-16 14:35:09
 */
@Controller
@RequestMapping("inspection")
public class InspectionManageController {

	@RequestMapping("config")
	public String config(HttpServletRequest request) {
		if (UserAgentUtils.isMobileAccess(request)) {
			return "";
		} else {
			return "";
		}
	}

}
