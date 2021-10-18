package com.chaoxing.activity.admin.controller.stat;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.stat.ActivityOrgStatDTO;
import com.chaoxing.activity.dto.stat.ActivityStatDTO;
import com.chaoxing.activity.service.activity.stat.ActivityStatQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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

    @Resource
    private WfwAreaApiService wfwAreaApiService;

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
    @RequestMapping("org/stat")
    public String orgActivityStat(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid()))));
        ActivityOrgStatDTO activityOrgStat = activityStatQueryService.orgActivityStat(realFid);
        model.addAttribute("fid", realFid);
        model.addAttribute("activityOrgStat", activityOrgStat);
        return "pc/stat/org-activity-stat";
    }

    /**区域下活动统计主页
    * @Description
    * @author huxiaolong
    * @Date 2021-05-11 15:19:55
    * @param request
    * @param model
    * @param fid
    * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("region/stat")
    public String regionActivityStat(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid()))));
        ActivityOrgStatDTO regionalActivityStat = activityStatQueryService.regionalActivityStat(realFid);
        List<WfwAreaDTO> wfwRegionalArchitectureTrees = wfwAreaApiService.listWfwAreaTreesByFid(realFid);
        model.addAttribute("fid", realFid);
        model.addAttribute("regionalActivityStat", regionalActivityStat);
        model.addAttribute("wfwRegionalArchitectureTrees", wfwRegionalArchitectureTrees);
        return "pc/stat/regional-activity-stat";
    }

}