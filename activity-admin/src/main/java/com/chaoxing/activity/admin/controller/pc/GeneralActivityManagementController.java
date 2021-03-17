package com.chaoxing.activity.admin.controller.pc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**通用活动管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagementController
 * @description
 * @blame wwb
 * @date 2020-12-08 19:06:17
 */
@Slf4j
@Controller
@RequestMapping({"general", ""})
public class GeneralActivityManagementController {

	@Resource
	private ActivityManageController activityManageController;

	/**活动管理主页
	 * @Description
	 * @author wwb
	 * @Date 2020-11-18 11:34:30
	 * @param model
	 * @param code 图书馆编码
	 * @param secondClassroomFlag 第二课堂标识
	 * @param strict
	 * @return java.lang.String
	 */
	@RequestMapping("")
	public String index(Model model, String code, @RequestParam(defaultValue = "0") Integer secondClassroomFlag, @RequestParam(defaultValue = "0") Integer strict) {
		return activityManageController.index(model, code, secondClassroomFlag, strict);
	}

	/**活动新增页面
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-25 10:19:16
	 * @param model
	 * @param request
	 * @param code
	 * @param secondClassroomFlag 第二课堂标识
	 * @return java.lang.String
	*/
	@GetMapping("activity/add")
	public String add(Model model, HttpServletRequest request, String code, @RequestParam(defaultValue = "0") Integer secondClassroomFlag) {
		return activityManageController.add(model, request, code, secondClassroomFlag);
	}

}
