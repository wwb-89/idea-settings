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
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("new")
    public RestRespDTO create(HttpServletRequest request) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Integer workId = workApiService.createDefault(loginUser.getUid(), loginUser.getFid());
        return RestRespDTO.success(workId);
    }

}