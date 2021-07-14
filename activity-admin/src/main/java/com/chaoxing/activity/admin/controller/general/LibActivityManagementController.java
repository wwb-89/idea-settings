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
public class LibActivityManagementController {

	@Resource
	private ActivityManagementController activityManagementController;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param model
	 * @param marketId
	 * @param code 图书馆编码
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param strict
	 * @return java.lang.String
	*/
	@RequestMapping("")
	public String index(Model model, Integer marketId, String code, Integer wfwfid, Integer unitId, Integer state, Integer fid, @RequestParam(defaultValue = "0") Integer strict) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
		return activityManagementController.index(model, marketId, code, realFid, strict);
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:26:18
	 * @param model
	 * @param request
	 * @param templateId
	 * @param code
	 * @return java.lang.String
	*/
	@GetMapping("activity/add")
	public String add(Model model, HttpServletRequest request, Integer templateId, String code) {
		return activityManagementController.add(request, model, templateId, code);
	}

}