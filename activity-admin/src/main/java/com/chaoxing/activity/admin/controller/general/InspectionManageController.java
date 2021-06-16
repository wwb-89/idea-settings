package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.UserActionTypeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.InspectionConfig;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**考核管理
 * @author wwb
 * @version ver 1.0
 * @className InspectionManageController
 * @description
 * @blame wwb
 * @date 2021-06-16 14:35:09
 */
@Controller
@RequestMapping("activity/{activityId}/inspection")
public class InspectionManageController {

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;

	@RequestMapping("config")
	public String config(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		// 可以管理活动
		Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
		model.addAttribute("activity", activity);
		// 考核配置
		InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		model.addAttribute("inspectionConfig", inspectionConfig);
		// 考核配置详情列表
		List<InspectionConfigDetail> inspectionConfigDetails = inspectionConfigQueryService.listDetailByConfig(inspectionConfig);
		model.addAttribute("inspectionConfigDetails", inspectionConfigDetails);
		// 用户行为
		List<UserActionTypeDTO> userActionTypes = UserActionTypeDTO.fromUserActionTypeEnum();
		model.addAttribute("userActionTypes", userActionTypes);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "";
		} else {
			return "pc/inspection/inspection-config";
		}
	}

}
