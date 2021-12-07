package com.chaoxing.activity.web.controller.pc;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingValidateService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xhl
 * @version ver 1.0
 * @className ActivityRatingController
 * @description
 * @blame xhl
 * @date 2021-03-11 10:50:34
 */
@Controller
@RequestMapping("activity/{activityId}/rating")
public class ActivityRatingController {

    @Resource
    private ActivityRatingQueryService activityRatingQueryService;
    @Resource
    private ActivityRatingValidateService activityRatingValidateService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityValidationService activityValidationService;

    /**
     * 活动评价首页
     * @param model
     * @return
     */
    @GetMapping("")
    public String index(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Activity activity = activityQueryService.getById(activityId);
        boolean canRating = false;
        boolean isManager = false;
        if (loginUser != null) {
            Integer uid = loginUser.getUid();
            canRating = activityRatingValidateService.submitRatingAble(activityId, uid);
            isManager = activityValidationService.isManageAble(activity, uid);
        }
        ActivityRating activityRating = activityRatingQueryService.getByActivityId(activityId);
        model.addAttribute("activity", activity);
        model.addAttribute("activityRating", activityRating);
        model.addAttribute("canRating", canRating);
        model.addAttribute("isManager", isManager);
        if (UserAgentUtils.isMobileAccess(request)) {
            // 管理端的域名
            model.addAttribute("adminDomain", DomainConstant.ADMIN);
            return "mobile/activity/rating/index";
        }
        return "pc/activity/rating/index";
    }
}
