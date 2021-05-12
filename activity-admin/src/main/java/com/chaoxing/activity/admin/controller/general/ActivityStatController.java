package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.stat.ActivityOrgStatDTO;
import com.chaoxing.activity.dto.stat.ActivityStatDTO;
import com.chaoxing.activity.service.activity.ActivityStatQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
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
@RequestMapping("activity")
public class ActivityStatController {

    @Resource
    private ActivityStatQueryService activityStatQueryService;

    /**活动统计主页
     * @Description 
     * @author wwb
     * @Date 2021-04-15 15:59:52
     * @param request
     * @param model
     * @param activityId
     * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("{activityId}/stat")
    public String index(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityStatDTO activityStat = activityStatQueryService.activityStat(activityId, loginUser);
        model.addAttribute("activityStat", activityStat);
        if (UserAgentUtils.isMobileAccess(request)) {
            return "mobile/stat/activity-stat";
        } else {
            return "pc/stat/activity-stat";
        }
    }
    /**机构下活动统计主页
    * @Description
    * @author huxiaolong
    * @Date 2021-05-11 15:19:55
    * @param request
    * @param model
    * @param fid
    * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("org/{fid}/stat")
    public String orgActivityStat(HttpServletRequest request, Model model, @PathVariable Integer fid) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        ActivityOrgStatDTO activityOrgStat = activityStatQueryService.orgActivityStat(fid);
        model.addAttribute("activityOrgStat", activityOrgStat);
        if (UserAgentUtils.isMobileAccess(request)) {
            return "mobile/stat/org-activity-stat";
        } else {
            return "pc/stat/org-activity-stat";
        }
    }

}