package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author xhl
 * @version ver 1.0
 * @className ActivityRatingManageController
 * @description
 * @blame xhl
 * @date 2021-03-11 16:43:05
 */
@Controller
@RequestMapping("activity/{activityId}/rating")
public class ActivityRatingManageController {

    @Resource
    private ActivityQueryService activityQueryService;

    /**
     * 评分设置页面
     * @param model
     * @return
     */
    @RequestMapping("setting")
    public String setting(Model model, @PathVariable Integer activityId){
        Activity activity = activityQueryService.getById(activityId);
        model.addAttribute("activity", activity);
        return "pc/rating/rating-setting";
    }

    /**
     * 审核页面
     * @param model
     * @param activityId
     * @return
     */
    @RequestMapping("audit")
    public String auditIndex(Model model, @PathVariable Integer activityId){
        Activity activity = activityQueryService.getById(activityId);
        model.addAttribute("activity", activity);
        return "pc/rating/rating-audit";
    }
}
