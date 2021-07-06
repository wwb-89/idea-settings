package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
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

    @PostMapping("design")
    public RestRespDTO index(Integer fid, Integer templateId) {

        return RestRespDTO.success();
    }
}
