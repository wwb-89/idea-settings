package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.module.ReadingApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**阅读管理
 * @Description
 * @author huxiaolong
 * @Date 2021-12-02 17:28:17
 * @className ReadingApiController
 * @return
 */
@RestController
@RequestMapping("api/reading")
public class ReadingApiController {

    @Resource
    private ReadingApiService readingApiService;

    /**创建一个阅读
    * @Description
    * @author huxiaolong
    * @Date 2021-09-02 16:34:11
    * @param request
    * @param activityName
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("new")
    public RestRespDTO create(HttpServletRequest request, String activityName) {
        return RestRespDTO.success(readingApiService.create(request, activityName));
    }

}