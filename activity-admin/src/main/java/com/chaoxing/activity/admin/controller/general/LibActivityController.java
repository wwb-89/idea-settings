package com.chaoxing.activity.admin.controller.general;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/** 图书馆活动管理
 * @author wwb
 * @version ver 1.0
 * @className LibActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-11-10 14:58:50
 */
@Controller
@RequestMapping({"lib", "bas", "edu"})
public class LibActivityController {

	@Resource
	private ActivityController activityManagementController;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param model
	 * @param marketId
	 * @param areaCode 图书馆编码
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param strict
	 * @param flag
	 * @return java.lang.String
	*/
	@RequestMapping("")
	public String index(Model model, Integer marketId, String areaCode, Integer wfwfid, Integer unitId, Integer state, Integer fid, @RequestParam(defaultValue = "0") Integer strict, String flag, @RequestParam(defaultValue = "0") Integer pageMode) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		return activityManagementController.index(model, marketId, realFid, strict, flag, areaCode, pageMode);
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:26:18
	 * @param model
	 * @param request
	 * @param templateId
	 * @param areaCode
	 * @return java.lang.String
	*/
	@GetMapping("activity/add")
	public String add(Model model, HttpServletRequest request, Integer templateId, String flag, String areaCode, Integer strict) {
		return activityManagementController.add(request, model, templateId, flag, areaCode, strict);
	}

}