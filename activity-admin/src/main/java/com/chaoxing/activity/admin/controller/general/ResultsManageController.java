package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.model.ActivityTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/** 考核管理
 * @author wwb
 * @version ver 1.0
 * @className ResultsManageController
 * @description
 * @blame wwb
 * @date 2021-03-15 16:05:22
 */
@Controller
@RequestMapping("activity/{activityId}/results/manage")
public class ResultsManageController {

	@Resource
	private TableFieldQueryService tableFieldQueryService;


	/**考核管理主页
	* @Description
	* @author huxiaolong
	* @Date 2021-06-24 10:11:49
	* @param request
	* @param model
	* @param activityId
	* @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping()
	public String index(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.RESULT_MANAGE, TableField.AssociatedType.ACTIVITY);
		List<ActivityTableField> activityTableFields = tableFieldQueryService.listActivityTableField(activityId, TableField.Type.RESULT_MANAGE, TableField.AssociatedType.ACTIVITY);
		Integer tableFieldId = null;
		if (CollectionUtils.isNotEmpty(tableFieldDetails)) {
			tableFieldId = tableFieldDetails.get(0).getTableFieldId();
		}
		model.addAttribute("activityId", activityId);
		model.addAttribute("tableFieldId", tableFieldId);
		model.addAttribute("tableFieldDetails", tableFieldDetails);
		model.addAttribute("activityTableFields", activityTableFields);
	 	if (UserAgentUtils.isMobileAccess(request)) {
	 		return "mobile/result/result-manage";
		}
 		return "pc/result/result-manage";
	}

	/**个人成绩行为记录页
	* @Description
	* @author huxiaolong
	* @Date 2021-06-28 18:45:02
	* @param request
	* @param model
	* @param activityId
	* @param uid
	* @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("person-grade")
	public String personalGrade(HttpServletRequest request, Model model, @PathVariable Integer activityId, Integer uid) {
		model.addAttribute("uid", uid);
		model.addAttribute("activityId", activityId);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/result/person-grade";
		}
		return "pc/result/person-grade";
	}

}