package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.volunteer.VolunteerService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**志愿服务单api前端控制器
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:01 下午
 * <p>
 */
@Controller
@RequestMapping("api/activity/volunteer")
public class VolunteerApiController {

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
    @GetMapping("{fid}/query")
    public RestRespDTO index(HttpServletRequest request, Model model,
                             @PathVariable Integer fid,
                             @RequestParam("serviceType") String serviceType) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        if (loginUser != null) {
            // todo
//            List<VolunteerServiceDTO> volunteerServiceDTOList = volunteerService.listServiceTimeLength(22651866, fid);
//            model.addAttribute("volunteerList", volunteerServiceDTOList);
        }
        return RestRespDTO.success(new ArrayList<>());
    }

}
