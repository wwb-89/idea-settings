package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.query.admin.ActivityRegionStatQueryDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityOrgStatDTO;
import com.chaoxing.activity.dto.stat.ActivityRegionalStatDTO;
import com.chaoxing.activity.model.ActivityStat;
import com.chaoxing.activity.service.activity.ActivityStatQueryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/12 2:14 下午
 * <p>
 */
@RestController
@RequestMapping("api/activity/stat")
public class ActivityStatApiController {

    @Resource
    private ActivityStatQueryService activityStatQueryService;

    /**机构活动统计查询
     * @Description
     * @author huxiaolong
     * @Date 2021-05-11 16:53:12
     * @param fid
     * @param startDate
     * @param endDate
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @PostMapping("/org/{fid}/query")
    public RestRespDTO getOrgActivityStatInfo(@PathVariable Integer fid, String startDate, String endDate) {
        ActivityOrgStatDTO activityOrgStat = activityStatQueryService.orgActivityStat(fid, startDate, endDate);
        return RestRespDTO.success(activityOrgStat);
    }

    /**机构活动TOP榜数据查询
     * @Description
     * @author huxiaolong
     * @Date 2021-05-12 17:17:56
     * @param queryParamStr
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @PostMapping("/top-activity")
    public RestRespDTO listTopActivity(String queryParamStr) {
        ActivityStatQueryDTO statQueryParams = JSON.parseObject(queryParamStr, ActivityStatQueryDTO.class);
        List<ActivityStat> topActivityStat = activityStatQueryService.listTopActivity(statQueryParams);
        return RestRespDTO.success(topActivityStat);
    }


    /**区域活动统计查询
     * @Description
     * @author huxiaolong
     * @Date 2021-05-11 16:53:12
     * @param fid
     * @param startDate
     * @param endDate
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @PostMapping("/region/{fid}/query")
    public RestRespDTO getRegionalActivityStatInfo(@PathVariable Integer fid, String startDate, String endDate) {
        ActivityOrgStatDTO activityOrgStat = activityStatQueryService.regionalActivityStat(fid, startDate, endDate);
        return RestRespDTO.success(activityOrgStat);
    }

    /**当前机构下区域活动统计
    * @Description
    * @author huxiaolong
    * @Date 2021-05-12 17:17:56
    * @param queryParamStr
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("/region/detail")
    public RestRespDTO listRegionStatDetail(String queryParamStr) {
        ActivityRegionStatQueryDTO queryParams = JSON.parseObject(queryParamStr, ActivityRegionStatQueryDTO.class);
        List<ActivityRegionalStatDTO> regionalStats = activityStatQueryService.listRegionStatDetail(queryParams);
        return RestRespDTO.success(regionalStats);
    }

    /**当前机构下下属机构各自的活动统计汇总
    * @Description
    * @author huxiaolong
    * @Date 2021-05-12 17:17:56
    * @param queryParamStr
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("/region/org/detail")
    public RestRespDTO listRegionOrgStatDetail(String queryParamStr) {
        ActivityRegionStatQueryDTO queryParams = JSON.parseObject(queryParamStr, ActivityRegionStatQueryDTO.class);
        List<ActivityRegionalStatDTO> regionalOrgStats = activityStatQueryService.listRegionOrgStatDetail(queryParams);
        return RestRespDTO.success(regionalOrgStats);
    }
}
