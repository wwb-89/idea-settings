package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author xhl
 * @version ver 1.0
 * @className WfwGroupApiController
 * @description
 * @blame xhl
 * @date 2021-03-10 14:57:52
 */
@RestController
@RequestMapping("api/wfw/group")
public class WfwGroupApiController {

    @Resource
    private WfwGroupApiService wfwGroupApiService;

    /**
     * 获取机构组织架构
     * @param request
     * @param gid
     * @return
     */
    @PostMapping("listGroup")
    public RestRespDTO listGroup(HttpServletRequest request, String gid) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<WfwGroupDTO> wfwGroups = wfwGroupApiService.getGroupByGid(loginUser.getFid(), gid);
        return RestRespDTO.success(wfwGroups);
    }
}
