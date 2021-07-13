package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.OrgRoleDTO;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.OrgConfig;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import com.chaoxing.activity.service.manager.WfwContactApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.org.OrgConfigService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/2 10:26 上午
 * <p>
 */

@Controller
@RequestMapping("activity/create/permission")
public class ActivityCreatePermissionController {

    @Resource
    private OrganizationalStructureApiService organizationalStructureApiService;
    @Resource
    private ActivityClassifyQueryService activityClassifyQueryService;
    @Resource
    private WfwGroupApiService wfwGroupApiService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private OrgConfigService orgConfigService;

    @LoginRequired
    @RequestMapping("")
    public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid()))));
        List<OrgRoleDTO> roleList = organizationalStructureApiService.listOrgRoles(realFid);
        List<ActivityClassify> classifyList = activityClassifyQueryService.listOrgAffiliation(realFid);
        OrgConfig orgConfig = orgConfigService.getByFid(realFid);
        String signUpScopeType = Optional.ofNullable(orgConfig).map(OrgConfig::getSignUpScopeType).orElse("");
        // 微服务组织架构
        List<WfwGroupDTO> wfwGroups;
        if (Objects.equals(signUpScopeType, OrgConfig.SignUpScopeType.CONTACTS.getValue())) {
            wfwGroups = wfwContactApiService.listUserContactOrgsByFid(realFid);
        }else {
            signUpScopeType = OrgConfig.SignUpScopeType.WFW.getValue();
            wfwGroups = wfwGroupApiService.listGroupByFid(realFid);
            wfwGroups = wfwGroupApiService.buildWfwGroups(wfwGroups);
        }
        model.addAttribute("fid", realFid);
        model.addAttribute("groupType", signUpScopeType);
        model.addAttribute("wfwGroups", wfwGroups);
        model.addAttribute("classifyList", classifyList);
        model.addAttribute("roleList", roleList);
        return "pc/permission/index";
    }
}
