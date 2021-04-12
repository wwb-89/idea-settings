package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.ActivityClassifyNew;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyNewQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyNewValidationService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

	@Resource
	private ActivityClassifyNewValidationService activityClassifyNewValidationService;
	@Resource
	private ActivityClassifyNewQueryService activityClassifyNewQueryService;

	/**活动类型管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-11 22:37:33
	 * @param request
	 * @param model
	 * @param activityMarketId 活动市场id
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, @RequestParam Integer activityMarketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyNewValidationService.manageAble(activityMarketId, loginUser);
		model.addAttribute("activityMarketId", activityMarketId);
		// 查询所有的活动类型
		List<ActivityClassifyNew> activityClassifyNews = activityClassifyNewQueryService.listByActivityMarketId(activityMarketId);
		model.addAttribute("activityClassifies", activityClassifyNews);
		return "pc/activity-classify";
	}

}
