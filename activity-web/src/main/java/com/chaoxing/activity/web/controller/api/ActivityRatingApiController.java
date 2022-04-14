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
import org.springframework.web.bind.annotation.*;

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

    /**评价列表
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:10:58
     * @param request
     * @param activityId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("list")
    public RestRespDTO listByActivityId(HttpServletRequest request, @RequestParam Integer activityId){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityRatingQueryDTO activityRatingQuery = new ActivityRatingQueryDTO();
        activityRatingQuery.setActivityId(activityId);
        if (loginUser != null) {
            activityRatingQuery.setUid(loginUser.getUid());
        }
        Page<ActivityRatingDetail> page = HttpServletRequestUtils.buid(request);
        activityRatingQueryService.paging(page, activityRatingQuery);
        return RestRespDTO.success(page);
    }

    /**新增评价
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:10:34
     * @param request
     * @param activityId
     * @param activityRatingDetailJsonStr
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("add")
    public RestRespDTO add(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam String activityRatingDetailJsonStr){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityRatingDetail activityRatingDetail = JSONObject.parseObject(activityRatingDetailJsonStr, ActivityRatingDetail.class);
        activityRatingDetail.setActivityId(activityId);
        activityRatingHandleService.addRating(activityRatingDetail, loginUser);
        return RestRespDTO.success();
    }

    /**更新评价
     * @Description 
     * @author wwb
     * @Date 2021-03-17 17:55:41
     * @param request
     * @param activityRatingDetailJsonStr
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("update")
    public RestRespDTO update(HttpServletRequest request, @RequestParam String activityRatingDetailJsonStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityRatingDetail activityRatingDetail = JSONObject.parseObject(activityRatingDetailJsonStr, ActivityRatingDetail.class);
        activityRatingHandleService.updateRating(activityRatingDetail, loginUser);
        return RestRespDTO.success();
    }

    /**删除评价
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:12:00
     * @param request
     * @param ratingId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("{ratingId}/delete")
    public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer ratingId) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        activityRatingHandleService.deleteRating(ratingId, loginUser);
        return RestRespDTO.success();
    }

    /**管理员删除评价，审核不通过
    * @Description
    * @author huxiaolong
    * @Date 2021-05-12 16:17:00
    * @param request
    * @param activityId
    * @param activityRatingDetailId
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("{ratingId}/reject")
    public RestRespDTO reject(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam Integer activityRatingDetailId){
        activityRatingHandleService.reject(activityId, activityRatingDetailId);
        return RestRespDTO.success();
    }

}