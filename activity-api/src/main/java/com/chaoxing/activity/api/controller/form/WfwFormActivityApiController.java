package com.chaoxing.activity.api.controller.form;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.service.queue.activity.WfwFormSyncActivityQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**万能表单活动服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityApiController
 * @description
 * @blame wwb
 * @date 2021-12-29 19:49:53
 */
@Slf4j
@RestController
@RequestMapping({"activity", "wfw/form"})
public class WfwFormActivityApiController {

    @Resource
    private WfwFormSyncActivityQueue wfwFormSyncActivityQueue;
    @Resource
    private WfwFormSyncActivityQueueService wfwFormSyncActivityQueueService;

    /** 万能表单数据新增/修改/删除后同步修改活动
     * @Description
     * @author huxiaolong
     * @Date 2021-08-26 16:46:53
     * @param activityFormSyncParam
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("sync/from/wfw-form")
    public RestRespDTO activitySyncOperate(@Valid ActivityCreateFromFormParamDTO activityFormSyncParam) {
        log.info("接收到万能表单的活动处理参数:{}", JSON.toJSONString(activityFormSyncParam));
        wfwFormSyncActivityQueue.push(activityFormSyncParam);
        return RestRespDTO.success();
    }

    @RequestMapping("add/validate")
    public JSONObject addValidate(Integer formId, Integer uid, Integer fid, String op, String formData) {
        boolean addAble = true;
        String message = "成功";
        FormDataDTO formDataDto = JSON.parseObject(formData, FormDataDTO.class);
        TimeScopeDTO activityTimeScope = ActivityCreateParamDTO.resolveActivityTime(formDataDto);
        if (activityTimeScope.getEndTime().compareTo(activityTimeScope.getStartTime()) <= 0) {
            addAble = false;
            message = "活动结束时间必须大于开始时间";
        }
        // 报名时间
        TimeScopeDTO signUpTimeScope = wfwFormSyncActivityQueueService.resolveSignUpTime(formDataDto);
        if (signUpTimeScope.getEndTime().compareTo(signUpTimeScope.getStartTime()) <= 0) {
            addAble = false;
            message = "报名结束时间必须大于开始时间";
        }
        JSONObject result = new JSONObject();
        result.put("result", addAble ? "YES" : "NO");
        result.put("message", message);
        log.info("校验结果:{}", result.toJSONString());
        return result;
    }

    /**通用表单配置发布状态更新接口
     * @Description 万能表单点击发布/下架掉用接口
     * @author huxiaolong
     * @Date 2021-11-13 01:12:47
     * @param fid
     * @param formId
     * @param uid
     * @param formUserId
     * @param marketId
     * @param flag
     * @param released
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("update/release-status/from/wfw-form")
    public RestRespDTO updateReleaseStatusFromWfwForm(Integer fid, Integer formId, Integer uid, Integer formUserId, Integer marketId, String flag, boolean released) {
        wfwFormSyncActivityQueueService.syncUpdateReleaseStatus(fid, formId, uid, formUserId, marketId, flag, released);
        return RestRespDTO.success();
    }

}