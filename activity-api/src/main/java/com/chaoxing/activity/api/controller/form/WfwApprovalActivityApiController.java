package com.chaoxing.activity.api.controller.form;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormDataItemDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwApprovalActivityCreateDTO;
import com.chaoxing.activity.service.queue.activity.WfwApprovalActivityCreateQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwApprovalActivityCreateQueueService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        List<FormDataItemDTO> formDataItems = JSON.parseArray(formData, FormDataItemDTO.class);
        // 活动时间
        TimeScopeDTO activityTimeScope = ActivityCreateParamDTO.resolveActivityTime(formDataItems);
        LocalDateTime activityStartTime = Optional.ofNullable(activityTimeScope.getStartTime()).orElseThrow(() -> new BusinessException("活动开始时间不能为空"));
        LocalDateTime activityEndTime = Optional.ofNullable(activityTimeScope.getEndTime()).orElseThrow(() -> new BusinessException("活动结束时间不能为空"));
        if (activityEndTime.compareTo(activityStartTime) <= 0) {
            addAble = false;
            message = "活动结束时间必须大于开始时间";
        }
        // 报名时间
        TimeScopeDTO signUpTimeScope = wfwApprovalActivityCreateQueueService.resolveSignUpTime(formDataItems);
        if (signUpTimeScope != null) {
            LocalDateTime signUpStartTime = Optional.ofNullable(signUpTimeScope.getStartTime()).orElseThrow(() -> new BusinessException("报名开始时间不能为空"));
            LocalDateTime signUpEndTime = Optional.ofNullable(signUpTimeScope.getEndTime()).orElseThrow(() -> new BusinessException("报名结束时间不能为空"));
            if (signUpEndTime.compareTo(signUpStartTime) <= 0) {
                addAble = false;
                message = "报名结束时间必须大于开始时间";
            }
        }
        // 签到时间
        TimeScopeDTO signInTimeScope = wfwApprovalActivityCreateQueueService.resolveSignInTime(formDataItems, null);
        if (signInTimeScope != null) {
            LocalDateTime signInStartTime = Optional.ofNullable(signInTimeScope.getStartTime()).orElseThrow(() -> new BusinessException("签到开始时间不能为空"));
            if (signInTimeScope.getEndTime() != null && signInTimeScope.getEndTime().compareTo(signInStartTime) <= 0) {
                addAble = false;
                message = "签到结束时间必须大于开始时间";
            }
        }
        // 签退时间
        TimeScopeDTO signOutTimeScope = wfwApprovalActivityCreateQueueService.resolveSignOutTime(formDataItems, null);
        if (signOutTimeScope != null) {
            LocalDateTime signOutStartTime = Optional.ofNullable(signOutTimeScope.getStartTime()).orElseThrow(() -> new BusinessException("签退开始时间不能为空"));
            if (signOutTimeScope.getEndTime() != null && signOutTimeScope.getEndTime().compareTo(signOutStartTime) <= 0) {
                addAble = false;
                message = "签退结束时间必须大于开始时间";
            }
        }
        JSONObject result = new JSONObject();
        result.put("result", addAble ? "YES" : "NO");
        result.put("message", message);
        log.info("校验结果:{}", result.toJSONString());
        return result;
    }

}