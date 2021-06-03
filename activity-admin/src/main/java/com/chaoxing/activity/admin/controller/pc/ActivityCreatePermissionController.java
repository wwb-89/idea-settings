package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgRoleDTO;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    private UcApiService ucApiService;

    @LoginRequired
    @RequestMapping("")
    public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Integer uid = loginUser.getUid();
        if (realFid == null) {
            realFid = loginUser.getFid();
        }
        if (ucApiService.isManager(realFid, uid)) {
            List<OrgRoleDTO> roleList = organizationalStructureApiService.listOrgRoles(realFid);
            List<ActivityClassify> classifyList = activityClassifyQueryService.listOrgOptionsByFid(realFid);
            // 微服务组织架构
            List<WfwGroupDTO> wfwGroups = wfwGroupApiService.getGroupByGid(realFid, 0);
            model.addAttribute("fid", realFid);
            model.addAttribute("wfwGroups", wfwGroups);
            model.addAttribute("classifyList", classifyList);
            model.addAttribute("roleList", roleList);
        }
        return "pc/manage/index";
    }
}
