package com.chaoxing.activity.admin.controller.stat;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormFilterDTO;
import com.chaoxing.activity.model.OrgTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.SchoolYearSemesterUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**机构统计
 * @author wwb
 * @version ver 1.0
 * @className OrgStatController
 * @description
 * @blame wwb
 * @date 2021-05-27 16:53:22
 */
@Controller
@RequestMapping("stat/org")
public class OrgStatController {

    @Resource
    private WfwGroupApiService wfwGroupApiService;

    @Resource
    private TableFieldQueryService tableFieldQueryService;

    @Resource
    private ActivityClassifyQueryService activityClassifyQueryService;
    @Resource
    private PassportApiService passportApiService;


    /**机构下的用户统计
     * @Description 
     * @author wwb
     * @Date 2021-05-27 17:03:15
     * @param request
     * @param model
     * @param wfwfid
     * @param unitId
     * @param state
     * @param fid
     * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("user")
    public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
        realFid = Optional.ofNullable(realFid).orElse(LoginUtils.getLoginUser(request).getFid());
        List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.USER_STAT, TableField.AssociatedType.ORG);
        model.addAttribute("tableFieldDetails", tableFieldDetails);
        List<OrgTableField> orgTableFields = tableFieldQueryService.listOrgTableField(realFid, TableField.Type.USER_STAT, TableField.AssociatedType.ORG);
        model.addAttribute("orgTableFields", orgTableFields);
        model.addAttribute("fid", realFid);
        Integer tableFieldId = Optional.ofNullable(tableFieldDetails).orElse(Lists.newArrayList()).stream().findFirst().map(TableFieldDetail::getTableFieldId).orElse(null);
        model.addAttribute("tableFieldId", tableFieldId);
        // 带层级（children）组织架构
        List<WfwGroupDTO> groups = wfwGroupApiService.listHierarchyGroupByFid(realFid);
        model.addAttribute("groups", groups);
        // 所有的组织架构
        List<WfwGroupDTO> allWfwGroups = wfwGroupApiService.listGroupByFid(realFid);
        model.addAttribute("allWfwGroups", allWfwGroups);
        // 机构名称
        String orgName = passportApiService.getOrgName(realFid);
        model.addAttribute("orgName", orgName);
        return "pc/stat/org-user-stat";
    }

    /**机构下的活动统计
    * @Description
    * @author huxiaolong
    * @Date 2021-05-28 16:23:12
    * @param request
    * @param model
    * @param wfwfid
    * @param unitId
    * @param state
    * @param fid
    * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("activity")
    public String activityStatIndex(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
        realFid = Optional.ofNullable(realFid).orElse(LoginUtils.getLoginUser(request).getFid());
        List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.ACTIVITY_STAT, TableField.AssociatedType.ORG);
        List<OrgTableField> orgTableFields = tableFieldQueryService.listOrgTableField(realFid, TableField.Type.ACTIVITY_STAT, TableField.AssociatedType.ORG);
        List<WfwFormFilterDTO> classifyOptions = activityClassifyQueryService.listOrgOptions(realFid);
        Integer tableFieldId = Optional.ofNullable(tableFieldDetails).orElse(Lists.newArrayList()).stream().findFirst().map(TableFieldDetail::getTableFieldId).orElse(null);
        // 微服务组织架构
        List<WfwGroupDTO> wfwGroups = wfwGroupApiService.listGroupByFid(realFid);

        model.addAttribute("startTime", SchoolYearSemesterUtils.currentSemesterStartTime().format(DateUtils.FULL_TIME_FORMATTER));
        model.addAttribute("endTime", LocalDateTime.now().format(DateUtils.FULL_TIME_FORMATTER));
        model.addAttribute("classifyOptions", classifyOptions);
        model.addAttribute("wfwGroups", wfwGroups);
        model.addAttribute("fid", realFid);
        model.addAttribute("tableFieldId", tableFieldId);
        model.addAttribute("tableFieldDetails", tableFieldDetails);
        model.addAttribute("orgTableFields", orgTableFields);
        return "pc/stat/org-activity-stat-summary";
    }

}