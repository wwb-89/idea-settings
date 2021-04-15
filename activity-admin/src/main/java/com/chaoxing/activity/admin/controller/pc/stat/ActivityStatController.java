package com.chaoxing.activity.admin.controller.pc.stat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**活动统计
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatController
 * @description
 * @blame wwb
 * @date 2021-04-15 15:57:59
 */
@Controller
@RequestMapping("stat/activity")
public class ActivityStatController {

    /**活动统计主页
     * @Description 
     * @author wwb
     * @Date 2021-04-15 15:59:52
     * @param request
     * @param model
     * @param activityId
     * @return java.lang.String
    */
    @RequestMapping("{activityId}")
    public String index(HttpServletRequest request, Model model, @PathVariable Integer activityId) {

        return "pc/stat/activity-stat";
    }

}