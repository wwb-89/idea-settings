package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.MoocApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**活动组织者管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagerController
 * @description
 * @blame wwb
 * @date 2021-03-23 15:40:45
 */
@Controller
@RequestMapping("activity/{activityId}/manager")
public class ActivityManagerManageController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private MoocApiService moocApiService;
	@Resource
	private PassportApiService passportApiService;

	@LoginRequired
	@RequestMapping
	public String index(@PathVariable Integer activityId, Model model, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = activityQueryService.getById(activityId);
		model.addAttribute("activity", activity);
		// 查询用户的机构列表
		List<Integer> fids = moocApiService.listUserFids(loginUser.getUid());
		List<OrgDTO> orgs = passportApiService.listOrg(fids);
		model.addAttribute("orgs", orgs);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/activity-manager";
		} else {
			return "pc/activity-manager";
		}
	}

}
