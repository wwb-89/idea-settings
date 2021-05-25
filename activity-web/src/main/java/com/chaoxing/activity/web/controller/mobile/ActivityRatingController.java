package com.chaoxing.activity.web.controller.mobile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author huxiaolong
 * @version ver 1.0
 * @className ActivityRatingController
 * @description
 * @blame huxiaolong
 * @date 2021-04-13 11:50:34
 */
@Controller
@RequestMapping("m/activity/{activityId}/rating")
public class ActivityRatingController {

    @Resource
    private ActivityRatingQueryService actRatingQueryService;

    /**
    * 新增活动评价
    * @author huxiaolong
    * 2021-04-15 10:33:41
    * @param model
    * @param activityId
    * @return java.lang.String
    */
    @GetMapping("add")
    public String addComment(Model model, @PathVariable Integer activityId) {
        model.addAttribute("activityId", activityId);
        return "mobile/activity/rating/write-comment";

    }


    /**
    * 编辑活动评价
    * @author huxiaolong
    * 2021-04-15 10:36:02
    * @param request
    * @param model
    * @param activityId
    * @param ratingId
    * @return java.lang.String
    */
    @GetMapping("edit")
    public String writeComment(HttpServletRequest request, Model model,
                               @PathVariable Integer activityId, @Param("ratingId") Integer ratingId) {
        ActivityRatingDetail actRatingDetail = actRatingQueryService.getDataByActIdDetailId(activityId, ratingId);
        model.addAttribute("activityId", activityId);
        model.addAttribute("rateItem", actRatingDetail);
        return "mobile/activity/rating/write-comment";

    }
}
