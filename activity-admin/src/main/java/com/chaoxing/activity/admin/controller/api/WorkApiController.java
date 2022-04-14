package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**作品征集
 * @author wwb
 * @version ver 1.0
 * @className WorkApiController
 * @description
 * @blame wwb
 * @date 2021-08-05 16:41:14
 */
@RestController
@RequestMapping("api/work")
public class WorkApiController {

    @Resource
    private WorkApiService workApiService;

    /**创建一个默认的作品征集
     * @Description 
     * @author wwb
     * @Date 2021-08-05 16:46:04
     * @param request
     * @param uid 如果活动不为空应该是活动的创建者id
     * @param fid 如果活动不为空应该是活动的创建者fid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("new")
    public RestRespDTO create(HttpServletRequest request, Integer uid, Integer fid) {
        if (uid == null) {
            LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
            uid = Optional.ofNullable(loginUser).map(LoginUserDTO::getUid).orElse(null);
            fid = Optional.ofNullable(loginUser).map(LoginUserDTO::getFid).orElse(null);
        }
        Integer workId = workApiService.createDefault(uid, fid);
        return RestRespDTO.success(workId);
    }

}