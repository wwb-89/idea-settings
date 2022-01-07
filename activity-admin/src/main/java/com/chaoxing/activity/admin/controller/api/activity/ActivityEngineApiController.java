package com.chaoxing.activity.admin.controller.api.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.service.activity.engine.ActivityEngineHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/6 2:17 下午
 * <p>
 */
@RestController
@RequestMapping("api/activity/engine")
public class ActivityEngineApiController {

    @Resource
    private ActivityEngineHandleService activityEngineHandleService;

    @PostMapping("component/submit")
    public RestRespDTO addCustomComponent(HttpServletRequest request, String componentStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Component component = JSON.parseObject(componentStr, Component.class);
        return RestRespDTO.success(activityEngineHandleService.handleCustomComponent(loginUser.getUid(), component));
    }

    /**发布模板(新增/修改)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 17:24:09
    * @param templateInfoStr
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @PostMapping("market/{marketId}/template/publish")
    public RestRespDTO publish(HttpServletRequest request, Integer fid, @PathVariable Integer marketId, String templateInfoStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityEngineDTO activityEngineDTO = JSON.parseObject(templateInfoStr, ActivityEngineDTO.class);
        activityEngineHandleService.handleEngineTemplate(fid, marketId, loginUser.getUid(), activityEngineDTO);
        return RestRespDTO.success();
    }
}
