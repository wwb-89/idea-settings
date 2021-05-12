package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.stat.ActivityOrgStatDTO;
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
    
    /**活动TOP榜数据查询
    * @Description 
    * @author huxiaolong
    * @Date 2021-05-12 17:17:56
    * @param fid
* @param queryParamsStr
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("/org/{fid}/top-activity")
    public RestRespDTO listTopActivity(@PathVariable Integer fid, String queryParamsStr) {
        JSONObject jsonObject = JSON.parseObject(queryParamsStr);
        List<Integer> activityIds = jsonObject.getJSONArray("activityIds").toJavaList(Integer.class);
        String sortField = jsonObject.getString("sortField");
        List<ActivityStat> topActivityStat = activityStatQueryService.listTopActivity(sortField, activityIds);

        return RestRespDTO.success(topActivityStat);
    }
}
