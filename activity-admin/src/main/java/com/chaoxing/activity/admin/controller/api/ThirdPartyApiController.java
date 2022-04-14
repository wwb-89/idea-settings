package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.uc.ClazzDTO;
import com.chaoxing.activity.service.manager.ThirdPartyApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/3 16:25
 * <p>
 */
@RestController
@RequestMapping("api/third-party")
public class ThirdPartyApiController {

    @Resource
    private ThirdPartyApiService thirdPartyApiService;


    /**根据第三方接口查询执教班级数据
    * @Description
    * @author huxiaolong
    * @Date 2021-09-03 16:42:48
    * @param url
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("query/teaching/clazz")
    public RestRespDTO searchDataFormThirdPartyUrl(String url) {
        List<ClazzDTO> clazzes = thirdPartyApiService.getDataFromThirdPartyUrl(url, ClazzDTO.class);
        return RestRespDTO.success(clazzes);
    }
}
