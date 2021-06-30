package com.chaoxing.activity.admin.controller.pc;

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
	private ActivityManageController activityManageController;

	/**活动管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param model
	 * @param code 图书馆编码
	 * @param wfwfid
	 * @param unitId
	 * @param state
	 * @param fid
	 * @param strict
	 * @param flag 活动标示。通用、第二课堂、双选会...
	 * @return java.lang.String
	*/
	@RequestMapping("")
	public String index(Model model, String code, Integer wfwfid, Integer unitId, Integer state, Integer fid, @RequestParam(defaultValue = "0") Integer strict, String flag) {
		Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
//		todo
//		return activityManageController.index(model, code, realFid,0, strict, flag);
		return activityManageController.newIndex(model, code, realFid,0, strict, flag);
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:26:18
	 * @param model
	 * @param request
	 * @param code
	 * @param flag
	 * @param strict
	 * @return java.lang.String
	*/
	@GetMapping("activity/add")
	public String add(Model model, HttpServletRequest request, String code, String flag, @RequestParam(defaultValue = "0") Integer strict) {
		return activityManageController.add(model, request, code, 0, flag, strict);
	}

}