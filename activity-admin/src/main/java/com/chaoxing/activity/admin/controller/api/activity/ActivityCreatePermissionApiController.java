package com.chaoxing.activity.admin.controller.api.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityCreatePermission;
import com.chaoxing.activity.service.activity.manager.ActivityCreatePermissionService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/2 10:26 上午
 * <p>
 */

@RestController
@RequestMapping("api/activity/create/permission")
public class ActivityCreatePermissionApiController {


    @Resource
    private ActivityCreatePermissionService activityCreatePermissionService;

    @LoginRequired
    @PostMapping("{roleId}")
    public RestRespDTO getPermissionByFidRoleId(Integer fid, Integer marketId, @PathVariable Integer roleId) {
        return RestRespDTO.success(activityCreatePermissionService.getPermissionByFidRoleId(fid, marketId, roleId));
    }

    @LoginRequired
    @PostMapping("add")
    public RestRespDTO add(HttpServletRequest request, ActivityCreatePermission activityCreatePermission) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        activityCreatePermission.setCreateUid(loginUser.getUid());
        activityCreatePermission.setUpdateUid(loginUser.getUid());
        activityCreatePermissionService.add(activityCreatePermission);
        return RestRespDTO.success();
    }

    @LoginRequired
    @PostMapping("edit")
    public RestRespDTO edit(HttpServletRequest request, ActivityCreatePermission activityCreatePermission) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        activityCreatePermission.setUpdateUid(loginUser.getUid());
        activityCreatePermissionService.edit(activityCreatePermission);
        return RestRespDTO.success();
    }

    @LoginRequired
    @PostMapping("batchConfig")
    public RestRespDTO batchConfig(HttpServletRequest request, String roleIds, String config) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityCreatePermission permissionConfig = JSON.parseObject(config, ActivityCreatePermission.class);
        List<Integer> roleIdList = JSONArray.parseArray(roleIds, Integer.class);
        activityCreatePermissionService.batchConfigPermission(loginUser.getUid(), roleIdList, permissionConfig);
        return RestRespDTO.success();
    }
}
