package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.OrgRoleDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.OrgConfig;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.org.OrgConfigService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.apache.commons.compress.utils.Lists;
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
    private ClassifyQueryService classifyQueryService;
    @Resource
    private WfwGroupApiService wfwGroupApiService;
    @Resource
    private WfwContactApiService wfwContactApiService;

    @LoginRequired
    @RequestMapping("")
    public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid, Integer marketId) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid()))));
        List<OrgRoleDTO> roleList = organizationalStructureApiService.listOrgRoles(realFid);
        List<Classify> classifies;
        if (marketId != null) {
            classifies = classifyQueryService.listMarketClassifies(marketId);
        } else {
            classifies = classifyQueryService.listOrgClassifies(realFid);
        }
        // 微服务组织架构
        List<WfwGroupDTO> wfwGroups = WfwGroupDTO.perfectWfwGroups(wfwGroupApiService.listGroupByFid(realFid));
        List<WfwGroupDTO> contactsGroups = wfwContactApiService.listUserContactOrgsByFid(realFid);
        model.addAttribute("fid", realFid);
        model.addAttribute("marketId", marketId);
        model.addAttribute("wfwGroups", wfwGroups);
        model.addAttribute("contactsGroups", contactsGroups);
        model.addAttribute("classifyList", classifies);
        model.addAttribute("roleList", roleList);
        return "pc/permission/index";
    }
}
