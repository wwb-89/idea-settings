package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.service.volunteer.VolunteerService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**志愿服务单api前端控制器
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:01 下午
 * <p>
 */
@RestController
@RequestMapping("api/activity/volunteer")
public class VolunteerApiController {

    @Resource
    private VolunteerService volunteerService;


    /***志愿服务单首页
    * @Description
    * @author huxiaolong
    * @Date 2021-05-19 14:04:06
    * @param request
    * @return java.lang.String
    */
    @LoginRequired
    @PostMapping("{fid}/query")
    public RestRespDTO index(HttpServletRequest request, @PathVariable Integer fid, @RequestParam("serviceType") String serviceType) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Integer uid = loginUser.getUid();
        List<VolunteerServiceDTO> volunteerServiceList = volunteerService.listServiceTimeLength(uid, fid, serviceType);
        return RestRespDTO.success(volunteerServiceList);
    }

}
