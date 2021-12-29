package com.chaoxing.activity.api.controller.form;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwApprovalActivityCreateDTO;
import com.chaoxing.activity.service.queue.activity.WfwApprovalActivityCreateQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwApprovalActivityApiController
 * @description
 * @blame wwb
 * @date 2021-12-29 15:42:06
 */
@Slf4j
@RestController
@RequestMapping("wfw/approval")
public class WfwApprovalActivityApiController {

    @Resource
    private WfwApprovalActivityCreateQueue wfwApprovalActivityCreateQueue;

    /**审批（申报）创建活动
     * @Description 
     * @author wwb
     * @Date 2021-12-29 15:44:13
     * @param deptId
     * @param formId
     * @param formUserId
     * @param flag
     * @param marketId
     * @param templateId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("add")
    public RestRespDTO add(@RequestParam Integer deptId, Integer formId, @RequestParam(value = "indexID") Integer formUserId, String flag, Integer marketId, Integer templateId) {
        WfwApprovalActivityCreateDTO formCreateActivity = WfwApprovalActivityCreateDTO.builder()
                .fid(deptId)
                .formId(formId)
                .formUserId(formUserId)
                .marketId(marketId)
                .flag(flag)
                .webTemplateId(templateId)
                .build();
        wfwApprovalActivityCreateQueue.push(formCreateActivity);
        return RestRespDTO.success();
    }

    /**新增校验
     * @Description 校验时间（活动时间、报名时间）
     * @author wwb
     * @Date 2021-12-29 16:21:57
     * @param formId
     * @param uid
     * @param fid
     * @param op
     * @param formData
     * @return com.alibaba.fastjson.JSONObject
    */
    @RequestMapping("add/validate")
    public JSONObject addValidate(Integer formId, Integer uid, Integer fid, String op, String formData) {
        boolean addAble = true;
        String message = "成功";
        FormDataDTO formDataDto = JSON.parseObject(formData, FormDataDTO.class);
        ActivityCreateParamDTO activity = ActivityCreateParamDTO.buildFromFormData(formDataDto, null, "");
        if (activity.getEndTimeStamp().compareTo(activity.getStartTimeStamp()) <= 0) {
            addAble = false;
            message = "活动结束时间必须大于开始时间";
        }
        JSONObject result = new JSONObject();
        result.put("result", addAble ? "YES" : "NO");
        result.put("message", message);
        log.info("校验结果:{}", result.toJSONString());
        return result;
    }

}