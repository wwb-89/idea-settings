package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**活动海报前端控制器
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/31 10:57 上午
 * <p>
 */
@Controller
@RequestMapping("activity/{activityId}/poster")
public class ActivityPosterController {

    @Resource
    private ActivityQueryService activityQueryService;

    @GetMapping("")
    public String index(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        SignUpCreateParamDTO signUp = activityQueryService.getActivitySignUp(activity.getSignId());
        Map<String, String> fieldCodeNameRelation = activityQueryService.getFieldCodeNameRelation(activity);
        model.addAttribute("fieldCodeNameRelation", fieldCodeNameRelation);
        model.addAttribute("activity", activity);
        model.addAttribute("signUp", signUp);
        if (UserAgentUtils.isMobileAccess(request)) {
            return "mobile/activity/poster/index";
        } else {
            return "pc/activity/poster/index";
        }
    }
}

