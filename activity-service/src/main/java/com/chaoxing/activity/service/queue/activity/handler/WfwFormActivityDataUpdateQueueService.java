package com.chaoxing.activity.service.queue.activity.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormActivityWriteBackDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.queue.activity.WfwFormActivityDataUpdateQueue;
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
        String data = packagePushUpdateData(fid, formId, buildWriteBackData(activity));
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
     * @param fid
     * @param formId
     * @param wfwFormActivityWriteBackData
     * @return java.lang.String
     */
    private String packagePushUpdateData(Integer fid, Integer formId, WfwFormActivityWriteBackDataDTO wfwFormActivityWriteBackData) {
        List<FormStructureDTO> formFieldInfos = wfwFormApiService.getFormStructure(formId, fid);
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

}