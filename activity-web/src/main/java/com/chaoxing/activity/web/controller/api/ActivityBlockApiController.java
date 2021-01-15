package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.sign.ActivityBlockDetailSignStatDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.CookieUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**活动块api
 * @author wwb
 * @version ver 1.0
 * @className ActivityBlockController
 * @description
 * @blame wwb
 * @date 2021-01-14 17:18:37
 */
@Controller
@RequestMapping("activity/block")
public class ActivityBlockApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private SignApiService signApiService;

	/**活动详情
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-14 17:20:41
	 * @param model
	 * @param request
	 * @param activityId
	 * @return java.lang.String
	*/
	@RequestMapping("detail")
	public String activityDetail(Model model, HttpServletRequest request, @RequestParam Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		model.addAttribute("activity", activity);
		Boolean enableSign = activity.getEnableSign();
		enableSign = Optional.ofNullable(enableSign).orElse(Boolean.FALSE);
		activity.setEnableSign(enableSign);
		Integer signId = activity.getSignId();
		ActivityBlockDetailSignStatDTO activityBlockDetailSignStat = new ActivityBlockDetailSignStatDTO();
		if (enableSign && signId != null) {
			// 查询报名签到
			Integer uid = CookieUtils.getUid(request);
			activityBlockDetailSignStat = signApiService.statActivityBlockDetail(signId, uid);
		}
		model.addAttribute("activityBlockDetailSignStat", activityBlockDetailSignStat);
		return "pc/block/activity-detail";
	}

}