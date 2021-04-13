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
     * 活动评价首页
     * @param model
     * @return
     */
    @GetMapping("writeComment")
    public String writeComment(HttpServletRequest request, Model model,
                               @PathVariable Integer activityId, @Param("ratingId") Integer ratingId) {
        if (ratingId != null) {
            ActivityRatingDetail actRatingDetail = actRatingQueryService.getDataByActIdDetailId(activityId, ratingId);
            model.addAttribute("activityId", activityId);
            model.addAttribute("rateItem", JSONObject.parseObject(JSON.toJSONString(actRatingDetail)));
        }
        return "mobile/activity/rating/write-comment";

    }
}
