package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className RedirectController
 * @description
 * @blame wwb
 * @date 2021-05-17 15:02:04
 */
@Controller
@RequestMapping("redirect")
public class RedirectController {

    @Resource
    private ActivityQueryService activityQueryService;

    /**根据表单行id重定向到活动的详情页面
     * @Description 
     * @author wwb
     * @Date 2021-05-17 15:15:34
     * @param formUserId
     * @return java.lang.String
    */
    @RequestMapping("activity-detail/from/form")
    public String form2ActivityDetail(Integer formUserId) {
        String url = "";
        Activity activity = activityQueryService.getByFormUserId(formUserId);
        if (activity != null) {
            url = activity.getPreviewUrl();
        }
        return "redirect:" + url;
    }

}