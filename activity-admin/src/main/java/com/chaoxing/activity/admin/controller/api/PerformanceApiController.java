package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Performance;
import com.chaoxing.activity.service.activity.performance.PerformanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/30 10:42 上午
 * <p>
 */
@RestController
@RequestMapping("api/activity/performance")
public class PerformanceApiController {

    @Resource
    private PerformanceService performanceService;

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-06-30 10:43:23
    * @param performance
    * @return RestRespDTO
    */
    @PostMapping("add")
    public RestRespDTO addPerformance(Performance performance) {
        performanceService.addPerformance(performance);
        return RestRespDTO.success();
    }
}
