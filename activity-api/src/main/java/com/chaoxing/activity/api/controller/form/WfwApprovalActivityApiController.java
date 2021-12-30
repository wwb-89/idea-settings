package com.chaoxing.activity.api.controller.form;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwApprovalActivityCreateDTO;
import com.chaoxing.activity.service.queue.activity.WfwApprovalActivityCreateQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwApprovalActivityCreateQueueService;
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
    @Resource
    private WfwApprovalActivityCreateQueueService wfwApprovalActivityCreateQueueService;

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
        // 活动时间
        TimeScopeDTO activityTimeScope = ActivityCreateParamDTO.resolveActivityTime(formDataDto);
        if (activityTimeScope.getEndTime().compareTo(activityTimeScope.getStartTime()) <= 0) {
            addAble = false;
            message = "活动结束时间必须大于开始时间";
        }
        // 报名时间
        TimeScopeDTO signUpTimeScope = wfwApprovalActivityCreateQueueService.resolveSignUpTime(formDataDto);
        if (signUpTimeScope != null && signUpTimeScope.getEndTime().compareTo(signUpTimeScope.getStartTime()) <= 0) {
            addAble = false;
            message = "报名结束时间必须大于开始时间";
        }
        // 签到时间
        TimeScopeDTO signInTimeScope = wfwApprovalActivityCreateQueueService.resolveSignInTime(formDataDto, null);
        if (signInTimeScope != null && signInTimeScope.getEndTime() != null && signInTimeScope.getEndTime().compareTo(signInTimeScope.getStartTime()) <= 0) {
            addAble = false;
            message = "签到结束时间必须大于开始时间";
        }
        // 签退时间
        TimeScopeDTO signOutTimeScope = wfwApprovalActivityCreateQueueService.resolveSignOutTime(formDataDto, null);
        if (signOutTimeScope != null && signOutTimeScope.getEndTime() != null && signOutTimeScope.getEndTime().compareTo(signOutTimeScope.getStartTime()) <= 0) {
            addAble = false;
            message = "签退结束时间必须大于开始时间";
        }
        JSONObject result = new JSONObject();
        result.put("result", addAble ? "YES" : "NO");
        result.put("message", message);
        log.info("校验结果:{}", result.toJSONString());
        return result;
    }

}