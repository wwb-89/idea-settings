package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ClassifyShowComponent;
import com.chaoxing.activity.service.activity.classify.component.ClassifyShowComponentHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**分类显示组件api服务
 * @author wwb
 * @version ver 1.0
 * @className ClassifyShowComponentApiController
 * @description
 * @blame wwb
 * @date 2022-01-05 16:01:56
 */
@Slf4j
@RestController
@RequestMapping("classify/component")
public class ClassifyShowComponentApiController {

    @Resource
    private ClassifyShowComponentHandleService classifyShowComponentHandleService;

    /**关联组件显示
     * @Description 
     * @author wwb
     * @Date 2022-01-05 16:10:22
     * @param request
     * @param classifyShowComponentJsonArray
     * @param templateId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("show/associated")
    public RestRespDTO showAssociated(HttpServletRequest request, String classifyShowComponentJsonArray, Integer templateId) {
        List<ClassifyShowComponent> classifyShowComponents = JSON.parseArray(classifyShowComponentJsonArray, ClassifyShowComponent.class);
        classifyShowComponentHandleService.associated(classifyShowComponents, templateId);
        return RestRespDTO.success();
    }

}