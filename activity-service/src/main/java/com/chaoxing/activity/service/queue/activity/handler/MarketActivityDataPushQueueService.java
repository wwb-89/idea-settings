package com.chaoxing.activity.service.queue.activity.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDataPushRecord;
import com.chaoxing.activity.model.DataPushConfig;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.v2.MarketActivityDataPushRecordService;
import com.chaoxing.activity.service.data.v2.DataPushConfigService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.queue.activity.MarketActivityDataPushQueue;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**活动市场下活动数据推送
 * @author wwb
 * @version ver 1.0
 * @className MarketActivityDataPushQueueService
 * @description
 * @blame wwb
 * @date 2021-10-29 17:00:56
 */
@Slf4j
@Service
public class MarketActivityDataPushQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private DataPushConfigService dataPushConfigService;
    @Resource
    private MarketActivityDataPushRecordService marketActivityDataPushRecordService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private WfwFormApiService wfwFormApiService;

    @Transactional(rollbackFor = Exception.class)
    public void handle(MarketActivityDataPushQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer activityId = queueParam.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer configId = queueParam.getDataPushConfigId();
        DataPushConfig dataPushConfig = dataPushConfigService.getById(configId);
        if (dataPushConfig == null) {
            return;
        }
        DataPushConfig.WayEnum way = DataPushConfig.WayEnum.fromValue(dataPushConfig.getWay());
        switch (way) {
            case WFW_FORM:
                handleWfwFormDataPush(activity, dataPushConfig);
                break;
            case URL:
                handleUrlDataPush(activity, dataPushConfig);
                break;
            default:
        }
    }

    private void handleWfwFormDataPush(Activity activity, DataPushConfig dataPushConfig) {
        // 表单id
        Integer formId = Optional.ofNullable(dataPushConfig.getWayValue()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null);
        if (formId == null) {
            return;
        }
        Integer configId = dataPushConfig.getId();
        Integer activityId = activity.getId();
        Integer formIdFid = dataPushConfig.getFid();
        Integer formUserId = null;
        // 查询已经推送的记录
        ActivityDataPushRecord activityDataPushRecord = marketActivityDataPushRecordService.get(activityId, configId);
        if (activityDataPushRecord != null) {
            formUserId = Optional.ofNullable(activityDataPushRecord.getTargetIdentify()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null);
            if (formUserId != null) {
                FormDataDTO formRecord = wfwFormApiService.getFormRecord(formUserId, formId, formIdFid);
                if (formRecord == null) {
                    // 表单中数据不存在
                    formUserId = null;
                }
            }
            if (formUserId == null) {
                marketActivityDataPushRecordService.delete(activityDataPushRecord.getId());
                activityDataPushRecord = null;
            }
        }
        Activity.StatusEnum status = Activity.StatusEnum.fromValue(activity.getStatus());
        switch (status) {
            case DELETED:
                // 删除
                marketActivityDataPushRecordService.delete(activityDataPushRecord.getId());
                if (formUserId != null) {
                    wfwFormApiService.deleteFormRecord(formUserId, formId);
                }
                break;
            default:
                String wfwFormData = generateWfwFormPushData(activity, formId, formIdFid);
                if (formUserId == null) {
                    // 新增
                    formUserId = wfwFormApiService.fillForm(formId, formIdFid, activity.getCreateUid(), wfwFormData);
                    marketActivityDataPushRecordService.add(ActivityDataPushRecord.builder()
                                    .activityId(activityId)
                                    .configId(configId)
                                    .marketId(activity.getMarketId())
                                    .targetIdentify(String.valueOf(formUserId))
                            .build());
                } else {
                    // 更新
                    wfwFormApiService.updateForm(formId, formUserId, wfwFormData);
                }
        }
    }

    private String generateWfwFormPushData(Activity activity, Integer formId, Integer fid) {
        List<FormStructureDTO> formFields = wfwFormApiService.getFormStructure(formId, fid);
        if (CollectionUtils.isEmpty(formFields)) {
            log.error("微服务表单:{}没有字段", formId);
            throw new BusinessException("微服务表单没有字段");
        }
        JSONArray result = new JSONArray();
        List<String> handledAlias = Lists.newArrayList();
        for (FormStructureDTO field : formFields) {
            String alias = field.getAlias();
            if (handledAlias.contains(alias)) {
                continue;
            } else {
                handledAlias.add(alias);
            }
            JSONObject item = new JSONObject();
            item.put("compt", field.getCompt());
            item.put("comptId", field.getId());
            JSONArray data = new JSONArray();
            result.add(item);
            // 活动id
            if ("activity_id".equals(alias)) {
                data.add(activity.getId());
                item.put("val", data);
                continue;
            }
            // 活动名称
            if ("activity_name".equals(alias)) {
                data.add(activity.getName());
                item.put("val", data);
                continue;
            }
            // 报名参与范围
            if ("sign_up_participate_scope".equals(alias)) {
                data.add(signApiService.getActivitySignParticipateScopeDescribe(activity.getSignId()));
                item.put("val", data);
                continue;
            }
            // 创建单位
            if ("create_org".equals(alias)) {
                data.add(activity.getCreateOrgName());
                item.put("val", data);
                continue;
            }
            // 活动分类
            if ("activity_classify".equals(alias)) {
                data.add(activity.getActivityClassifyName());
                item.put("val", data);
                continue;
            }
            // 活动积分
            if ("activity_integral".equals(alias)) {
                data.add(activity.getIntegral());
                item.put("val", data);
                continue;
            }
            // 单位
            if ("unit".equals(alias)) {
                data.add("积分");
                item.put("val", data);
                continue;
            }
            // 活动预览
            if ("preview_url".equals(alias)) {
                data.add(activity.getPreviewUrl());
                item.put("val", data);
                continue;
            }
            // 发起人
            if ("create_user".equals(alias)) {
                JSONObject user = new JSONObject();
                user.put("id", activity.getCreateUid());
                user.put("name", activity.getCreateUserName());
                data.add(user);
                item.put("idNames", data);
                continue;
            }
            // 活动状态
            if ("activity_status".equals(alias)) {
                Integer status = activity.getStatus();
                data.add(Activity.getStatusDescription(status));
                item.put("val", data);
            }
        }
        return result.toJSONString();
    }

    private void handleUrlDataPush(Activity activity, DataPushConfig dataPushConfig) {

    }

}