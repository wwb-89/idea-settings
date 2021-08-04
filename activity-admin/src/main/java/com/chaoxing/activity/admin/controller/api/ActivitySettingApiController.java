package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/4 16:30
 * <p>
 */

@RestController
@RequestMapping("api/activity/setting")
public class ActivitySettingApiController {

    @Resource
    private ActivityHandleService activityHandleService;

    /**活动基本信息修改
     * @Description
     * @author huxiaolong
     * @Date 2021-08-04 15:23:28
     * @param request
     * @param activityJsonStr
     * @param participateScopeJsonStr
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @LoginRequired
    @PostMapping("basic-info")
    public RestRespDTO edit(HttpServletRequest request, String activityJsonStr, String participateScopeJsonStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityUpdateParamDTO activityUpdateParamDto = JSON.parseObject(activityJsonStr, ActivityUpdateParamDTO.class);
        List<WfwAreaDTO> wfwRegionalArchitectures = JSON.parseArray(participateScopeJsonStr, WfwAreaDTO.class);
        activityHandleService.updateActivityBasicInfo(activityUpdateParamDto, wfwRegionalArchitectures, loginUser);
        return RestRespDTO.success(activityUpdateParamDto);
    }

    /**报名设置修改
     * @Description
     * @author huxiaolong
     * @Date 2021-08-04 15:23:53
     * @param request
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @LoginRequired
    @PostMapping("sign-up")
    public RestRespDTO edit(HttpServletRequest request, Integer activityId, String sucTemplateComponentIdStr, String signJsonStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        SignCreateParamDTO signAddEdit = JSON.parseObject(signJsonStr, SignCreateParamDTO.class);
        List<Integer> sucTemplateComponentIds = JSONArray.parseArray(sucTemplateComponentIdStr, Integer.class);
        activityHandleService.updateSignUp(activityId, sucTemplateComponentIds, signAddEdit, loginUser);
        return RestRespDTO.success();
    }



}
