package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.dto.UserGradeDTO;
import com.chaoxing.activity.model.ActivityTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.service.user.action.UserActionRecordQueryService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
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
@RequestMapping("activity/results/manage/{activityId}")
public class ResultsManageController {

	@Resource
	private TableFieldQueryService tableFieldQueryService;

	@Resource
	private UserResultQueryService userResultQueryService;
	@Resource
	private UserActionRecordQueryService userActionRecordQueryService;

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
		return "pc/result/result-manage";
	}

	@LoginRequired
	@RequestMapping("person-grade")
	public String personalGrade(Model model, @PathVariable Integer activityId, Integer uid) {
		UserGradeDTO userGrade = userActionRecordQueryService.getUserGrade(uid, activityId);
		model.addAttribute("userGrade", userGrade);
		return "pc/result/person-grade";
	}




}