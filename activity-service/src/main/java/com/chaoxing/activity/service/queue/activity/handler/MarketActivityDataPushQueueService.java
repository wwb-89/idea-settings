package com.chaoxing.activity.service.queue.activity.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.dto.manager.cloud.CloudImageDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.wfwform.v2.WfwFormDataAddParamDTO;
import com.chaoxing.activity.dto.manager.wfwform.v2.WfwFormDataUpdateParamDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.data.v2.DataPushConfigService;
import com.chaoxing.activity.service.data.v2.MarketActivityDataPushRecordService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.manager.wfw.v2.WfwFormNewApiService;
import com.chaoxing.activity.service.queue.activity.MarketActivityDataPushQueue;
import com.chaoxing.activity.service.tag.TagQueryService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.enums.ActivitySystemFieldEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
    /** 新增/修改数据使用 */
    @Resource
    private WfwFormNewApiService wfwFormNewApiService;
    @Resource
    private ActivityComponentValueService activityComponentValueService;
    @Resource
    private CloudApiService cloudApiService;
    @Resource
    private TagQueryService tagQueryService;

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
        // 查询万能表单数据推送配置
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
                String wfwFormData = generateWfwFormPushData(activity, formId, formIdFid, configId);
                if (formUserId == null) {
                    // 新增
                    WfwFormDataAddParamDTO wfwFormDataAddParam = WfwFormDataAddParamDTO.build(formId, wfwFormData, activity.getCreateUid(), formIdFid);
                    formUserId = wfwFormNewApiService.dataAdd(wfwFormDataAddParam);
                    marketActivityDataPushRecordService.add(ActivityDataPushRecord.builder()
                                    .activityId(activityId)
                                    .configId(configId)
                                    .marketId(activity.getMarketId())
                                    .targetIdentify(String.valueOf(formUserId))
                            .build());
                } else {
                    // 更新
                    WfwFormDataUpdateParamDTO wfwFormDataUpdateParam = WfwFormDataUpdateParamDTO.build(formUserId, wfwFormData, formIdFid);
                    wfwFormNewApiService.dataUpdate(wfwFormDataUpdateParam);
                }
        }
    }

    /**生成万能表单需要推送的数据
     * @Description 
     * @author wwb
     * @Date 2021-12-10 14:21:28
     * @param activity
     * @param formId
     * @param fid
     * @param dataPushConfigId
     * @return java.lang.String
    */
    private String generateWfwFormPushData(Activity activity, Integer formId, Integer fid, Integer dataPushConfigId) {
        List<FormStructureDTO> formFields = wfwFormApiService.getFormStructure(formId, fid);
        if (CollectionUtils.isEmpty(formFields)) {
            log.error("微服务表单:{}没有字段", formId);
            throw new BusinessException("微服务表单没有字段");
        }
        List<DataPushFormConfig> dataPushFormConfigs = dataPushConfigService.listFormConfigByConfigId(dataPushConfigId);
        boolean isCustomDataPush = CollectionUtils.isNotEmpty(dataPushFormConfigs);
        if (isCustomDataPush) {
            return generateCustomWfwFormPushData(activity, formFields, dataPushFormConfigs);
        }
        return generateDefaultWfwFormPushData(activity, formFields);
    }

    /**生成万能表单需要推送的数据（通用版本，没有配置字段对应的）
     * @Description 
     * @author wwb
     * @Date 2021-12-10 14:22:01
     * @param activity
     * @param formFields
     * @return java.lang.String
    */
    private String generateDefaultWfwFormPushData(Activity activity, List<FormStructureDTO> formFields) {
        // 查询市场有没有配置万能表单的推送配置明细
        JSONArray result = new JSONArray();
        List<String> handledAlias = Lists.newArrayList();
        for (FormStructureDTO field : formFields) {
            String alias = field.getAlias();
            if (handledAlias.contains(alias)) {
                continue;
            } else {
                handledAlias.add(alias);
            }
            JSONObject item = generateActivityField(activity, field, Maps.newHashMap());
            result.add(item);
        }
        return result.toJSONString();
    }

    private String generateCustomWfwFormPushData(Activity activity, List<FormStructureDTO> formFields, List<DataPushFormConfig> dataPushFormConfigs) {
        // 查看是否有自定义组件的值需要推送
        List<DataPushFormConfig> customComponentDataPushFormConfigs = dataPushFormConfigs.stream().filter(DataPushFormConfig::getCustomField).collect(Collectors.toList());
        boolean isHasCustomComponent = customComponentDataPushFormConfigs.size() > 0;
        Map<String, ActivityComponentValueDTO> componentIdStrActivityValueMap = Maps.newHashMap();
        if (isHasCustomComponent) {
            // 查询活动的自定义组件的值
            List<ActivityComponentValueDTO> activityComponentValues = activityComponentValueService.listActivityComponentValues(activity.getId(), activity.getTemplateId());
            componentIdStrActivityValueMap = activityComponentValues.stream().collect(Collectors.toMap(v -> String.valueOf(v.getComponentId()), v -> v, (v1, v2) -> v2));
        }
        Map<String, String> formFieldAliasActivityValueMap = Maps.newHashMap();
        for (DataPushFormConfig customComponentDataPushFormConfig : customComponentDataPushFormConfigs) {
            String dataField = customComponentDataPushFormConfig.getDataField();
            ActivityComponentValueDTO activityComponentValue = componentIdStrActivityValueMap.get(dataField);
            formFieldAliasActivityValueMap.put(customComponentDataPushFormConfig.getFormFieldAlias(), Optional.ofNullable(activityComponentValue).map(ActivityComponentValueDTO::getValue).orElse(""));
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
            JSONObject item = generateActivityField(activity, field, formFieldAliasActivityValueMap);
            result.add(item);
        }
        return result.toJSONString();
    }

    private JSONObject generateActivityField(Activity activity, FormStructureDTO field, Map<String, String> formFieldAliasActivityValueMap) {
        JSONObject item = new JSONObject();
        item.put("compt", field.getCompt());
        item.put("id", field.getId());
        JSONArray data = new JSONArray();
        String alias = field.getAlias();
        ActivitySystemFieldEnum activitySystemFieldEnum = ActivitySystemFieldEnum.fromValue(alias);
        JSONObject valueJsonObject = new JSONObject();
        data.add(valueJsonObject);
        if (activitySystemFieldEnum != null) {
            String address = Optional.ofNullable(activity.getAddress()).orElse("");
            address += Optional.ofNullable(activity.getDetailAddress()).orElse("");
            switch (activitySystemFieldEnum) {
                case ACTIVITY_ID:
                    valueJsonObject.put("val", activity.getId());
                    item.put("values", data);
                    break;
                case ACTIVITY_NAME:
                    valueJsonObject.put("val", activity.getName());
                    item.put("values", data);
                    break;
                case ACTIVITY_START_TIME:
                    valueJsonObject.put("val", activity.getStartTime().format(DATE_TIME_FORMATTER));
                    item.put("values", data);
                    break;
                case ACTIVITY_END_TIME:
                    valueJsonObject.put("val", activity.getEndTime().format(DATE_TIME_FORMATTER));
                    item.put("values", data);
                    break;
                case ACTIVITY_COVER:
                    String coverCloudId = activity.getCoverCloudId();
                    CloudImageDTO cloudImage = cloudApiService.getImage(coverCloudId);
                    if (cloudImage != null) {
                        valueJsonObject.put("name", cloudImage.getFileName());
                        valueJsonObject.put("suffix", cloudImage.getSuffix());
                        valueJsonObject.put("objectId", cloudImage.getObjectId());
                        valueJsonObject.put("size", cloudImage.getLength());
                        item.put("values", data);
                    }
                    break;
                case ACTIVITY_TYPE:
                    Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(activity.getActivityType());
                    valueJsonObject.put("val", Optional.ofNullable(activityTypeEnum).map(Activity.ActivityTypeEnum::getName).orElse(""));
                    item.put("values", data);
                    break;
                case ACTIVITY_ADDRESS:
                    valueJsonObject.put("val", address);
                    item.put("values", data);
                    break;
                case LOCATION:
                    BigDecimal lng = activity.getLongitude();
                    BigDecimal lat = activity.getDimension();
                    if (lng != null && lat != null) {
                        valueJsonObject.put("lng", lng);
                        valueJsonObject.put("lat", lat);
                        valueJsonObject.put("address", address);
                        item.put("values", data);
                    }
                    break;
                case SIGN_UP_PARTICIPATE_SCOPE:
                    valueJsonObject.put("val", signApiService.getActivitySignParticipateScopeDescribe(activity.getSignId()));
                    item.put("values", data);
                    break;
                case CREATE_ORG:
                    valueJsonObject.put("val", activity.getCreateOrgName());
                    item.put("values", data);
                    break;
                case ORGANISERS:
                    valueJsonObject.put("val", activity.getOrganisers());
                    item.put("values", data);
                    break;
                case ACTIVITY_CLASSIFY:
                    valueJsonObject.put("val", activity.getActivityClassifyName());
                    item.put("values", data);
                    break;
                case ACTIVITY_INTEGRAL:
                    valueJsonObject.put("val", activity.getIntegral());
                    item.put("values", data);
                    break;
                case UNIT:
                    valueJsonObject.put("val", "积分");
                    item.put("values", data);
                    break;
                case PREVIEW_URL:
                    valueJsonObject.put("val", activity.getPreviewUrl());
                    item.put("values", data);
                    break;
                case CREATE_USER:
                    valueJsonObject.put("puid", activity.getCreateUid());
                    valueJsonObject.put("uname", activity.getCreateUserName());
                    item.put("values", data);
                    break;
                case ACTIVITY_STATUS:
                    Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(activity.getStatus());
                    valueJsonObject.put("val", Optional.ofNullable(statusEnum).map(Activity.StatusEnum::getName).orElse(""));
                    item.put("values", data);
                    break;
                case TAGS:
                    List<Tag> tags = tagQueryService.listActivityAssociateTag(activity.getId());
                    if (CollectionUtils.isNotEmpty(tags)) {
                        String tagNames = String.join(CommonConstant.LINK_CHAR, tags.stream().map(Tag::getName).collect(Collectors.toList()));
                        valueJsonObject.put("val", tagNames);
                        item.put("values", data);
                    }
                    break;
                case INTRODUCTION:
                    ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activity.getId());
                    valueJsonObject.put("val", Optional.ofNullable(activityDetail).map(ActivityDetail::getIntroduction).filter(StringUtils::isNotBlank).orElse(""));
                    item.put("values", data);
                    break;
                default:
            }
        } else {
            boolean existAlias = formFieldAliasActivityValueMap.containsKey(alias);
            if (existAlias) {
                String value = formFieldAliasActivityValueMap.get(alias);
                valueJsonObject.put("val", value);
                item.put("values", data);
            }
        }
        return item;
    }

    private void handleUrlDataPush(Activity activity, DataPushConfig dataPushConfig) {

    }

}