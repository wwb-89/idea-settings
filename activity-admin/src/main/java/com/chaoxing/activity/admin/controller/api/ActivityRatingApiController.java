package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingHandleService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xhl
 * @version ver 1.0
 * @className ActivityRatingApiController
 * @description
 * @blame xhl
 * @date 2021-03-11 17:23:43
 */
@RestController
@RequestMapping("api/activity/rating")
public class ActivityRatingApiController {

    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private ActivityRatingQueryService activityRatingQueryService;
    @Resource
    private ActivityRatingHandleService activityRatingHandleService;
    @Resource
    private ActivityValidationService activityValidationService;

    /**
     * 更新评分设置
     * @param request
     * @param activityJsonStr
     * @return
     */
    @PostMapping("setting")
    public RestRespDTO setting(HttpServletRequest request, String activityJsonStr){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Activity activity = JSONObject.parseObject(activityJsonStr, Activity.class);
        activityHandleService.updateRatingConfig(activity.getId(), activity.getOpenRating(), activity.getRatingNeedAudit(), loginUser);
        return RestRespDTO.success();
    }

    /**
     * 获取审核列表
     * @param request
     * @param activityId
     * @return
     */
    @PostMapping("audit/list")
    public RestRespDTO listAuditByActivityId(HttpServletRequest request, @RequestParam Integer activityId){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        activityValidationService.manageAble(activityId, loginUser.getUid());
        Page<ActivityRatingDetail> page = HttpServletRequestUtils.buid(request);
        activityRatingQueryService.pagingWaitAudit(page, activityId);
        return RestRespDTO.success(page);
    }

    /**通过审核
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:04:20
     * @param request
     * @param activityId
     * @param activityRatingDetailId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("audit/pass")
    public RestRespDTO pass(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam Integer activityRatingDetailId){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        activityRatingHandleService.pass(loginUser, activityId, activityRatingDetailId);
        return RestRespDTO.success();
    }

    /**不通过审核
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:04:34
     * @param request
     * @param activityId
     * @param activityRatingDetailId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("audit/reject")
    public RestRespDTO reject(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam Integer activityRatingDetailId){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        activityRatingHandleService.reject(loginUser, activityId, activityRatingDetailId);
        return RestRespDTO.success();
    }

    /**批量通过审核
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:04:59
     * @param request
     * @param activityId
     * @param ratingDetailIdArr
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("audit/pass/batch")
    public RestRespDTO batchPass(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam("ratingDetailIds[]") Integer[] ratingDetailIdArr){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<Integer> ratingDetailIds = new ArrayList<>(Arrays.asList(ratingDetailIdArr));
        activityRatingHandleService.batchPass(activityId, ratingDetailIds, loginUser);
        return RestRespDTO.success();
    }

    /**批量不通过审核
     * @Description 
     * @author wwb
     * @Date 2021-03-17 20:05:21
     * @param request
     * @param activityId
     * @param ratingDetailIdArr
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("audit/reject/batch")
    public RestRespDTO batchReject(HttpServletRequest request, @RequestParam Integer activityId, @RequestParam("ratingDetailIds[]") Integer[] ratingDetailIdArr){
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<Integer> ratingDetailIds = new ArrayList<>(Arrays.asList(ratingDetailIdArr));
        activityRatingHandleService.batchReject(activityId, ratingDetailIds, loginUser);
        return RestRespDTO.success();
    }
}
