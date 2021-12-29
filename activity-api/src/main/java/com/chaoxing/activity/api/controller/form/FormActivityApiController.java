package com.chaoxing.activity.api.controller.form;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwApprovalActivityCreateDTO;
import com.chaoxing.activity.service.queue.activity.WfwApprovalActivityCreateQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormActivityApiController
 * @description
 * @blame wwb
 * @date 2021-05-11 14:51:41
 */
@Slf4j
@RestController
@RequestMapping("form/activity")
@Deprecated
public class FormActivityApiController {

    @Resource
    private WfwApprovalActivityCreateQueue formActivityCreateQueueService;

    /**创建活动
     * @Description 通过活动申报来创建活动
     * 根据fid、表单id、表单记录id查询表单的数据
     * 判断是审核通过则创建一个活动（当活动不存在的时候）
     * @author wwb
     * @Date 2021-05-10 16:26:47
     * @param deptId
     * @param formId
     * @param formUserId
     * @param marketId 活动市场id
     * @param templateId 需要使用的模版id
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("create")
    public RestRespDTO formCreateActivity(@RequestParam Integer deptId, Integer formId, @RequestParam(value = "indexID") Integer formUserId, String flag, Integer marketId, Integer templateId) {
        WfwApprovalActivityCreateDTO formCreateActivity = WfwApprovalActivityCreateDTO.builder()
                .fid(deptId)
                .formId(formId)
                .formUserId(formUserId)
                .marketId(marketId)
                .flag(flag)
                .webTemplateId(templateId)
                .build();
        formActivityCreateQueueService.push(formCreateActivity);
        return RestRespDTO.success();
    }

    /**表单审核
     * @Description 
     * @author wwb
     * @Date 2021-08-03 14:17:46
     * @param request
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("audit")
    public RestRespDTO audit(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("接收到表单数据审核信息: {}", JSON.toJSONString(parameterMap));
        return RestRespDTO.success();
    }

}
