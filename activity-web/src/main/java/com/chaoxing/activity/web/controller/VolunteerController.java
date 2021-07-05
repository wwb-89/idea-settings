package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.model.OrgConfig;
import com.chaoxing.activity.service.org.OrgConfigService;
import com.chaoxing.activity.service.volunteer.VolunteerService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**志愿服务单前端控制器
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:01 下午
 * <p>
 */
@Controller
@RequestMapping("volunteer")
public class VolunteerController {

    @Resource
    private VolunteerService volunteerService;
    @Resource
    private OrgConfigService orgConfigService;

    /***志愿服务单首页
    * @Description
    * @author huxiaolong
    * @Date 2021-05-19 14:04:06
    * @param request
    * @param model
    * @return java.lang.String
    */
    @LoginRequired
    @GetMapping
    public String index(HttpServletRequest request, Model model, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(state).orElse(fid);
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Integer uid = loginUser.getUid();
        List<VolunteerServiceDTO> volunteerServiceDTOList = volunteerService.listServiceTimeLength(uid, realFid);
        List<String> serviceTypeList = volunteerService.listVolunteerServiceType(realFid);
        model.addAttribute("fid", realFid);
        model.addAttribute("volunteerList", volunteerServiceDTOList);
        model.addAttribute("serviceTypeList", serviceTypeList);
        // 时长申报的url
        OrgConfig orgConfig = orgConfigService.getByFid(fid);
        String creditAppealUrl = Optional.ofNullable(orgConfig).map(OrgConfig::getCreditAppealUrl).orElse("");
        model.addAttribute("creditAppealUrl", creditAppealUrl);
        if (UserAgentUtils.isMobileAccess(request)) {
            return "mobile/volunteer/service-list";
        }
        return "pc/volunteer/service-list";
    }

}
