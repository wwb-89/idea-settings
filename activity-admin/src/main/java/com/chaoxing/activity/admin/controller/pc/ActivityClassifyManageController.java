package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**活动类型管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyManageController
 * @description
 * @blame wwb
 * @date 2021-04-11 22:33:40
 */
@Controller
@RequestMapping("activity/classify")
public class ActivityClassifyManageController {

	/**活动类型管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-11 22:37:33
	 * @param request
	 * @param model
	 * @param activityMarketId
	 * @return java.lang.String
	*/
	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, @RequestParam Integer activityMarketId) {
		model.addAttribute("activityMarketId", activityMarketId);
		return "pc/activity-classify";
	}

}
