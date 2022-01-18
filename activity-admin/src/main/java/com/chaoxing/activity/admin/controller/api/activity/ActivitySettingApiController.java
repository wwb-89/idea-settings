package com.chaoxing.activity.admin.controller.api.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.create.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.SignUpCondition;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuHandleService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.apache.commons.lang3.StringUtils;
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
    @Resource
    private ActivityMenuQueryService activityMenuQueryService;
    @Resource
    private ActivityMenuHandleService activityMenuHandleService;
    @Resource
    private SignApiService signApiService;

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
    public RestRespDTO basicInfoEdit(HttpServletRequest request, String activityJsonStr, String participateScopeJsonStr, String releaseClassIdJsonStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityUpdateParamDTO activityUpdateParamDto = JSON.parseObject(activityJsonStr, ActivityUpdateParamDTO.class);
        List<Integer> releaseClassIds = StringUtils.isBlank(releaseClassIdJsonStr) ? null : JSON.parseArray(releaseClassIdJsonStr, Integer.class);
        List<WfwAreaDTO> wfwRegionalArchitectures = StringUtils.isBlank(participateScopeJsonStr) ? null : JSON.parseArray(participateScopeJsonStr, WfwAreaDTO.class);

        Integer signId = activityUpdateParamDto.getSignId();
        SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
        if (signId != null) {
            sign = signApiService.getCreateById(signId);
        }
        activityHandleService.edit(activityUpdateParamDto, sign, wfwRegionalArchitectures, releaseClassIds, loginUser);
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
    public RestRespDTO signUpEdit(HttpServletRequest request, Integer activityId, String sucTemplateComponentIdStr, String signUpConditionStr, String signJsonStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        SignCreateParamDTO signAddEdit = JSON.parseObject(signJsonStr, SignCreateParamDTO.class);
        List<Integer> sucTemplateComponentIds = JSONArray.parseArray(sucTemplateComponentIdStr, Integer.class);
        List<SignUpCondition> signUpConditions = JSONArray.parseArray(signUpConditionStr, SignUpCondition.class);
        activityHandleService.updateSignUp(activityId, sucTemplateComponentIds, signUpConditions, signAddEdit, loginUser);
        return RestRespDTO.success();
    }

    /**更新模块设置
    * @Description
    * @author huxiaolong
    * @Date 2021-08-06 17:09:54
    * @param activityId
    * @param menus
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("menu")
    public RestRespDTO updateMenus(Integer activityId, String menus) {
        List<String> menuList = JSONArray.parseArray(menus, String.class);
        activityMenuHandleService.configActivityMenu(activityId, menuList);
        return RestRespDTO.success();
    }






}
