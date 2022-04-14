package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.query.ActivityReleasePlatformActivityQueryDTO;
import com.chaoxing.activity.dto.activity.query.result.ActivityReleasePlatformActivityQueryResultDTO;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**区域api服务
 * @author wwb
 * @version ver 1.0
 * @className AreaApiController
 * @description
 * @blame wwb
 * @date 2021-12-02 15:43:03
 */
@Slf4j
@RestController
@RequestMapping("area")
public class AreaApiController {

    @Resource
    private ActivityQueryService activityQueryService;

    /**活动发布平台区域创建的活动列表
     * @Description 
     * @author wwb
     * @Date 2021-12-02 15:49:05
     * @param activityReleasePlatformQuery
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/created/to-activity-release-platform")
    public RestRespDTO listActivityReleasePlatformAreaCreated(ActivityReleasePlatformActivityQueryDTO activityReleasePlatformQuery) {
        List<ActivityReleasePlatformActivityQueryResultDTO> activityReleasePlatformActivityQueryResults = activityQueryService.listAreaCreated(activityReleasePlatformQuery);
        return RestRespDTO.success(activityReleasePlatformActivityQueryResults);
    }

}