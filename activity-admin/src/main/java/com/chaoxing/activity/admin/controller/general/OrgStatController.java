package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.model.OrgTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;


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
        if (realFid == null) {
            LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
            realFid = loginUser.getFid();
        }
        List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.USER_STAT, TableField.AssociatedType.ORG);
        model.addAttribute("tableFieldDetails", tableFieldDetails);
        List<OrgTableField> orgTableFields = tableFieldQueryService.listOrgTableField(realFid, TableField.Type.USER_STAT, TableField.AssociatedType.ORG);
        model.addAttribute("orgTableFields", orgTableFields);
        model.addAttribute("fid", realFid);
        Integer tableFieldId = null;
        if (CollectionUtils.isNotEmpty(tableFieldDetails)) {
            tableFieldId = tableFieldDetails.get(0).getTableFieldId();
        }
        model.addAttribute("tableFieldId", tableFieldId);
        // 机构的组织架构
        List<WfwGroupDTO> groups = wfwGroupApiService.getGroupByGid(realFid, 0);
        if (CollectionUtils.isNotEmpty(groups)) {
            for (WfwGroupDTO group : groups) {
                group.setGroupLevel(1);
            }
        }
        model.addAttribute("groups", groups);
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
        if (realFid == null) {
            LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
            realFid = loginUser.getFid();
        }
        List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.ACTIVITY_STAT, TableField.AssociatedType.ORG);
        List<OrgTableField> orgTableFields = tableFieldQueryService.listOrgTableField(realFid, TableField.Type.ACTIVITY_STAT, TableField.AssociatedType.ORG);
//        Page<ActivityStatSummaryDTO> activityStatSummaryPage = activityStatSummaryQueryService.activityStatSummaryPage(new Page<>(), realFid);
        Integer tableFieldId = null;
        if (CollectionUtils.isNotEmpty(tableFieldDetails)) {
            tableFieldId = tableFieldDetails.get(0).getTableFieldId();
        }
        // 微服务组织架构
        List<WfwGroupDTO> wfwGroups = wfwGroupApiService.getGroupByFid(realFid);
        model.addAttribute("wfwGroups", wfwGroups);
        model.addAttribute("fid", realFid);
        model.addAttribute("tableFieldId", tableFieldId);
        model.addAttribute("tableFieldDetails", tableFieldDetails);
        model.addAttribute("orgTableFields", orgTableFields);
//        model.addAttribute("activityStatSummaryPage", activityStatSummaryPage);
        return "pc/stat/org-activity-stat-summary";
    }

}