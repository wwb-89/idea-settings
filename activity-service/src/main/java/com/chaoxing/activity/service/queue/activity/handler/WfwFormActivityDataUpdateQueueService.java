package com.chaoxing.activity.service.queue.activity.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormActivityWriteBackDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.queue.activity.WfwFormActivityDataUpdateQueue;
import com.chaoxing.activity.util.WfwFormUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**万能表单关联活动数据更新队列服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityDataUpdateQueueService
 * @description
 * @blame wwb
 * @date 2021-11-22 18:05:03
 */
@Slf4j
@Service
public class WfwFormActivityDataUpdateQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private WfwFormApiService wfwFormApiService;

    public void handle(WfwFormActivityDataUpdateQueue.QueueParamDTO queueParam) {
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer fid = queueParam.getFid();
        Integer formId = queueParam.getFormId();
        Integer formUserId = queueParam.getFormUserId();
        String data = packagePushUpdateData(buildWriteBackData(activity), formUserId, formId, fid);
        wfwFormApiService.updateForm(formId, formUserId, data);
    }

    private WfwFormActivityWriteBackDataDTO buildWriteBackData(Activity activity) {
        String signUpStatus = "";
        SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
        if (signStat != null && CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
            if (signStat.getSignUpStartTime() != null && signStat.getSignUpEndTime() != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime startTime = signStat.getSignUpStartTime();
                LocalDateTime endTime = signStat.getSignUpEndTime();
                if (startTime.isAfter(now)) {
                    signUpStatus = "未开始" ;
                } else if (now.isAfter(endTime)) {
                    signUpStatus = "已结束";
                } else {
                    signUpStatus = "报名中";
                }
            }
        }
        Boolean released = Optional.ofNullable(activity.getReleased()).orElse(false);
        return WfwFormActivityWriteBackDataDTO.builder()
                .activityId(activity.getId())
                .activityStatus(Activity.StatusEnum.fromValue(activity.getStatus()).getName())
                .activityReleaseStatus(released ? "已发布" : "未发布")
                .signUpStatus(signUpStatus)
                .previewUrl(activity.getPreviewUrl())
                .build();
    }

    /**封装回写数据
     * @Description
     * @author huxiaolong
     * @Date 2021-08-26 18:16:25
     * @param wfwFormActivityWriteBackData
     * @param formUserId
     * @param formId
     * @param fid
     * @return java.lang.String
     */
    private String packagePushUpdateData(WfwFormActivityWriteBackDataDTO wfwFormActivityWriteBackData, Integer formUserId, Integer formId, Integer fid) {
        List<FormStructureDTO> formFieldInfos = wfwFormApiService.getFormStructure(formId, fid);
        // 如果数据没发生改变则忽略（不更新）
        if (!isWfwFormDataChanged(fid, formId, formUserId, wfwFormActivityWriteBackData)) {
            return null;
        }
        JSONArray result = new JSONArray();
        for (FormStructureDTO formInfo : formFieldInfos) {
            String alias = formInfo.getAlias();
            JSONObject item = new JSONObject();
            item.put("id", formInfo.getId());
            item.put("compt", formInfo.getCompt());
            item.put("comptId", formInfo.getId());
            item.put("alias", alias);
            JSONArray data = new JSONArray();
            if (Objects.equals(alias, "activity_id")) {
                data.add(wfwFormActivityWriteBackData.getActivityId());
                item.put("val", data);
                result.add(item);
            } else if (Objects.equals(alias, "status")) {
                data.add(wfwFormActivityWriteBackData.getActivityStatus());
                item.put("val", data);
                result.add(item);
            } else if (Objects.equals(alias, "sign_up_status")) {
                String signUpStatus = wfwFormActivityWriteBackData.getSignUpStatus();
                if (StringUtils.isNotBlank(signUpStatus)) {
                    data.add(signUpStatus);
                    item.put("val", data);
                    result.add(item);
                }
            } else if (Objects.equals(alias, "preview_url")) {
                String previewUrl = wfwFormActivityWriteBackData.getPreviewUrl();
                if (StringUtils.isNotBlank(previewUrl)) {
                    data.add(previewUrl);
                    item.put("val", data);
                    result.add(item);
                }
            } else if (Objects.equals("release_status", alias)) {
                data.add(wfwFormActivityWriteBackData.getActivityReleaseStatus());
                item.put("val", data);
                result.add(item);
            }
        }
        if (result.isEmpty()) {
            // 没有配置任何别名则放过
            return null;
        }
        return result.toJSONString();
    }

    private boolean isWfwFormDataChanged(Integer fid, Integer formId, Integer formUserId, WfwFormActivityWriteBackDataDTO wfwFormActivityWriteBackData) {
        log.info("根据fid:{}, formId:{}, formUserId:{}, 数据:{} 验证万能表单活动是否需要更新 start", fid,formId, formUserId, JSON.toJSONString(wfwFormActivityWriteBackData));
        try {
            // 查询表单记录
            FormDataDTO formRecord = wfwFormApiService.getFormRecord(formUserId, formId, fid);
            if (formRecord == null) {
                log.info("根据fid:{}, formId:{}, formUserId:{}, 数据:{} 验证万能表单活动是否需要更新:表单记录不存在", fid,formId, formUserId, JSON.toJSONString(wfwFormActivityWriteBackData) );
                return false;
            }
            /** 活动id是否改变 */
            if (WfwFormUtils.isExistField(formRecord, "activity_id")) {
                String activityId = formRecord.getStringValue("activity_id");
                if (!Objects.equals(String.valueOf(wfwFormActivityWriteBackData.getActivityId()), activityId)) {
                    log.info("万能表单活动:{} 活动id改变 {} -> {}", wfwFormActivityWriteBackData.getActivityId(), activityId, wfwFormActivityWriteBackData);
                    return true;
                }
            }
            /** 活动状态是否改变 */
            if (WfwFormUtils.isExistField(formRecord, "status")) {
                String status = formRecord.getStringValue("status");
                if (!Objects.equals(wfwFormActivityWriteBackData.getActivityStatus(), status)) {
                    log.info("万能表单活动:{} 活动状态改变 {} -> {}}", wfwFormActivityWriteBackData.getActivityId(), status, wfwFormActivityWriteBackData.getActivityStatus());
                    return true;
                }
            }
            /** 报名状态是否改变 */
            if (WfwFormUtils.isExistField(formRecord, "sign_up_status")) {
                String signUpStatus = formRecord.getStringValue("sign_up_status");
                if (!Objects.equals(wfwFormActivityWriteBackData.getSignUpStatus(), signUpStatus) && StringUtils.isNotBlank(wfwFormActivityWriteBackData.getSignUpStatus())) {
                    log.info("万能表单活动:{} 报名状态改变 {} -> {}}", wfwFormActivityWriteBackData.getActivityId(), signUpStatus, wfwFormActivityWriteBackData.getSignUpStatus());
                    return true;
                }
            }
            /** 预览地址是否改变 */
            if (WfwFormUtils.isExistField(formRecord, "preview_url")) {
                String previewUrl = formRecord.getStringValue("preview_url");
                if (!Objects.equals(wfwFormActivityWriteBackData.getPreviewUrl(), previewUrl)) {
                    log.info("万能表单活动:{} 浏览地址改变 {} -> {}}", wfwFormActivityWriteBackData.getActivityId(), previewUrl, wfwFormActivityWriteBackData.getPreviewUrl());
                    return true;
                }
            }
            /** 发布状态是否改变 */
            if (WfwFormUtils.isExistField(formRecord, "release_status")) {
                String releaseStatus = formRecord.getStringValue("release_status");
                if (!Objects.equals(wfwFormActivityWriteBackData.getActivityReleaseStatus(), releaseStatus)) {
                    log.info("万能表单活动:{} 发布状态改变 {} -> {}}", wfwFormActivityWriteBackData.getActivityId(), releaseStatus, wfwFormActivityWriteBackData.getActivityReleaseStatus());
                    return true;
                }
            }
            log.info("根据fid:{}, formId:{}, formUserId:{}, 数据:{} 验证万能表单活动是否需要更新:数据没有改变", fid,formId, formUserId, JSON.toJSONString(wfwFormActivityWriteBackData));
            return false;
        } finally {
            log.info("根据fid:{}, formId:{}, formUserId:{}, 数据:{} 验证万能表单活动是否需要更新 end", fid,formId, formUserId, JSON.toJSONString(wfwFormActivityWriteBackData));
        }
    }

}