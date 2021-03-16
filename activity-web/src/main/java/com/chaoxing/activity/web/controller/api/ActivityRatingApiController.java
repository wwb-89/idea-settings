package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.query.ActivityRatingQueryDTO;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.rating.ActivityRatingHandleService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xhl
 * @version ver 1.0
 * @className ActivityRatingApiController
 * @description
 * @blame xhl
 * @date 2021-03-11 11:47:17
 */
@RestController
@RequestMapping("api/activity/rating")
public class ActivityRatingApiController {

    @Resource
    private ActivityRatingQueryService activityRatingQueryService;
    @Resource
    private ActivityRatingHandleService activityRatingHandleService;

    /**
     * 评分列表
     * @param request
     * @param activityId
     * @return
     */
    @RequestMapping("list")
    public RestRespDTO listByActivityId(HttpServletRequest request, @RequestParam Integer activityId){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityRatingQueryDTO activityRatingQuery = new ActivityRatingQueryDTO();
        activityRatingQuery.setActivityId(activityId);
        if(loginUser!=null){
            activityRatingQuery.setUid(loginUser.getUid());
        }
        Page<ActivityRatingDetail> page = HttpServletRequestUtils.buid(request);
        activityRatingQueryService.listByActivityId(page, activityRatingQuery);
        return RestRespDTO.success(page);
    }

    /**
     * 保存
     * @param request
     * @param activityId
     * @param activityRatingDetailJsonStr
     * @return
     */
    @PostMapping("add")
    public RestRespDTO add(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam String activityRatingDetailJsonStr){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityRatingDetail activityRatingDetail = JSONObject.parseObject(activityRatingDetailJsonStr, ActivityRatingDetail.class);
        activityRatingDetail.setActivityId(activityId);
        activityRatingHandleService.add(activityRatingDetail, loginUser);
        return RestRespDTO.success();
    }
}
