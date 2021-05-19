package com.chaoxing.activity.web.controller.pc;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.service.volunteer.VolunteerService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**志愿服务单前端控制器
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:01 下午
 * <p>
 */
@Controller
@RequestMapping("api/activity/volunteer")
public class ActivityVolunteerController {

    @Resource
    private VolunteerService volunteerService;


    /***志愿服务单首页
    * @Description
    * @author huxiaolong
    * @Date 2021-05-19 14:04:06
    * @param request
    * @param model
    * @return java.lang.String
    */
    @GetMapping("")
    public String index(HttpServletRequest request, Model model, Integer fid) {

        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        if (loginUser != null) {
            List<VolunteerServiceDTO> volunteerServiceDTOList = volunteerService.listServiceTimeLength(loginUser.getUid(), fid);
        }

        return "pc/activity/volunteer";
    }

}
