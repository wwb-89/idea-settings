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
import com.google.common.collect.Lists;
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
@RequestMapping("activity/inspection")
public class InspectionController {

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;

	@RequestMapping("config")
	public String config(HttpServletRequest request, Model model, boolean isClone, Integer activityId, Integer configId) {
		if (isClone || activityId != null) {
			return toConfigIndexByActivityId(request, model, activityId, isClone);
		}
		return toConfigIndexByConfigId(request, model, configId);

	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-22 10:59:44
	 * @param request
	 * @param model
	 * @param activityId 非克隆时，在活动主页进入考核管理设置，activityId进行校验是否可以进行管理；在克隆时，查询activityId下的考核配置，克隆配置内容
	 * @param isClone 默认false，当克隆活动的时候，此时为true
	 * @return java.lang.String
	 */
	private String toConfigIndexByActivityId(HttpServletRequest request, Model model, Integer activityId, boolean isClone) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		if (!isClone) {
			// 可以管理活动
			Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
			model.addAttribute("activity", activity);
		}
		// 考核配置
		InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		// 考核配置详情列表
		List<InspectionConfigDetail> inspectionConfigDetails = inspectionConfigQueryService.listDetailByConfig(inspectionConfig);
		if (isClone) {
			inspectionConfig.setId(null);
			inspectionConfig.setActivityId(null);
			inspectionConfigDetails.forEach(v -> v.setId(null));
			inspectionConfigDetails.forEach(v -> v.setConfigId(null));
		}
		model.addAttribute("inspectionConfig", inspectionConfig);
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

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-22 11:01:17
	 * @param request
	 * @param model
	 * @param configId 活动创建编辑页面，活动创建页面，configId为null，设置默认的考核配置返回页面；编辑页面，configId不为空，查询configId对应的考核配置返回页面
	 * @return java.lang.String
	 */
	private String toConfigIndexByConfigId(HttpServletRequest request, Model model, Integer configId) {
		// 考核配置
		InspectionConfig inspectionConfig = InspectionConfig.buildDefault(null);
		if (configId != null) {
			inspectionConfig = inspectionConfigQueryService.getByConfigId(configId);
		}
		model.addAttribute("inspectionConfig", inspectionConfig);
		// 考核配置详情列表
		List<InspectionConfigDetail> inspectionConfigDetails = Lists.newArrayList(InspectionConfigDetail.buildDefault(configId));
		if (inspectionConfig.getId() != null) {
			inspectionConfigDetails = inspectionConfigQueryService.listDetailByConfig(inspectionConfig);
		}
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
