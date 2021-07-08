package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.service.activity.engine.ActivityEngineHandleService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    private ActivityEngineQueryService activityEngineQueryService;

    @Resource
    private ActivityEngineHandleService activityEngineHandleService;


    /**获取模板详细信息(模板信息、模板组件、组件列表)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 17:24:12
    * @param fid
    * @param templateId
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("info")
    public RestRespDTO listOrgComponent(Integer fid, Integer templateId) {
        return RestRespDTO.success(activityEngineQueryService.findEngineTemplateInfo(fid, templateId));
    }

    /**发布模板(新增/修改)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 17:24:09
    * @param templateInfoStr
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("publish")
    public RestRespDTO publish(String templateInfoStr) {
        ActivityEngineDTO activityEngineDTO = JSON.parseObject(templateInfoStr, ActivityEngineDTO.class);
        activityEngineHandleService.handleEngineTemplate(activityEngineDTO);
        return RestRespDTO.success();
    }
}
