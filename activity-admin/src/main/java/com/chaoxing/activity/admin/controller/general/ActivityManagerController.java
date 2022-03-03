package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityManager;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**活动管理员
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagerController
 * @description
 * @blame wwb
 * @date 2021-03-23 15:40:45
 */
@Controller
@RequestMapping("activity/{activityId}/manager")
public class ActivityManagerController {

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityManagerService activityManagerService;
	@Resource
	private WfwContactApiService wfwContactApiService;
	@Resource
	private ActivityMenuQueryService activityMenuQueryService;

	/**管理员主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 14:41:22
	 * @param activityId
	 * @param model
	 * @param request
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping
	public String index(@PathVariable Integer activityId, Model model, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer operateUid = loginUser.getUid();
		Activity activity = activityValidationService.manageAbleRelax(activityId, operateUid);
		model.addAttribute("activity", activity);
		List<OrgDTO> orgs = wfwContactApiService.listUserHaveContactsOrg(operateUid);
		model.addAttribute("orgs", orgs);
		// 查询以选择的uid列表
		List<Integer> managerUids = activityManagerService.listUid(activityId);
		// todo 管理员页面菜单需要替换
		model.addAttribute("menus", activityMenuQueryService.listActivityEnableBackendMenus(activityId));
		model.addAttribute("managerUids", managerUids);
		model.addAttribute("photoDomain", DomainConstant.PHOTO);
		model.addAttribute("mainDomain", DomainConstant.MAIN);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/activity-manager";
		} else {
			return "pc/activity-manager";
		}
	}

	/**移动端管理者菜单权限配置页面
	* @Description
	* @author huxiaolong
	* @Date 2021-09-28 14:18:08
	* @param model
	* @param request
	* @param activityId
	* @param uid	被操作用户的uid
	* @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("{uid}/menu")
	public String managerMenuView(Model model, HttpServletRequest request, @PathVariable Integer activityId, @PathVariable Integer uid) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer loginUid = loginUser.getUid();
		boolean isCreator = activityValidationService.isCreator(activityId, loginUid);
		if (!isCreator) {
			ActivityManager loginManager = activityManagerService.getByActivityUid(activityId, loginUid);
			String canAccessMenu = Optional.ofNullable(loginManager).map(ActivityManager::getMenu).orElse(null);
			if (StringUtils.isNotBlank(canAccessMenu) && !canAccessMenu.contains(ActivityMenuEnum.BackendMenuEnum.SETTING.getValue())) {
				throw new BusinessException("无权限");
			}
		}
		ActivityManager activityManager = activityManagerService.getByActivityUid(activityId, uid);
		model.addAttribute("isCreator", isCreator);
		model.addAttribute("activityId", activityId);
		model.addAttribute("manager", activityManager);
		model.addAttribute("menus", activityMenuQueryService.listActivityEnableBackendMenus(activityId));
		return "mobile/activity-manager-menu";
	}

}
