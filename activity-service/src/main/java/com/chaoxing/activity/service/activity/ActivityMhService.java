package com.chaoxing.activity.service.activity;

import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.util.MhDataBuildUtil;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/6 2:11 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class ActivityMhService {

    @Resource
    private SignApiService signApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityComponentValueService activityComponentValueService;
    @Resource
    private ActivityCoverUrlSyncService activityCoverUrlSyncService;


    /**封装活动数据为门户标准接收格式
     * @Description
     * @author huxiaolong
     * @Date 2021-09-26 17:57:46
     * @param activities
     * @return com.alibaba.fastjson.JSONArray
     */
    public JSONArray packageActivities(List<Activity> activities, JSONObject urlParams) {
        JSONArray activityJsonArray = new JSONArray();
        if (CollectionUtils.isEmpty(activities)) {
            return activityJsonArray;
        }
        Map<Integer, Integer> activityTemplateMap = activities.stream().filter(v -> v.getTemplateId() != null).collect(Collectors.toMap(Activity::getId, Activity::getTemplateId, (v1, v2) -> v2));
        List<Integer> activityIds = activities.stream().map(Activity::getId).collect(Collectors.toList());
        List<Integer> signIds = activities.stream().map(Activity::getSignId).collect(Collectors.toList());
        Map<Integer, SignStatDTO> signStatMap = signApiService.statSignSignUps(signIds).stream().collect(Collectors.toMap(SignStatDTO::getId, v -> v, (v1, v2) -> v2));
        Map<Integer, List<ActivityComponentValueDTO>> activityComponentValuesMap = activityComponentValueService.listActivityComponentValues(activityTemplateMap);

        Map<Integer, String> introductionMap = activityQueryService.listDetailByActivityIds(activityIds)
                .stream()
                .collect(Collectors.toMap(ActivityDetail::getActivityId, v -> HtmlUtil.cleanHtmlTag(v.getIntroduction()).replaceAll(HtmlUtil.NBSP, " "), (v1, v2) -> v2));

        for (Activity record : activities) {
            Map<String, String> fieldCodeNameMap = activityQueryService.getFieldCodeNameRelation(record);
            SignStatDTO signStat = signStatMap.get(record.getSignId());

            // 活动
            JSONObject activity = new JSONObject();
            Integer activityId = record.getId();
            activity.put("id", activityId);
            activity.put("type", 3);
            activity.put("orsUrl", record.getPreviewUrl());
            JSONArray fields = new JSONArray();
            activity.put("fields", fields);
            int fieldFlag = 0;
            // 封面
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_cover", "封面"), activityCoverUrlSyncService.getCoverUrl(record), fieldFlag));
            // 活动名称
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_name", "名称"), record.getName(), ++fieldFlag));
            Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(record.getActivityType());
            String activityAddress = record.getAddress();
            if (StringUtils.isNotBlank(activityAddress)) {
                String detailAddress = record.getDetailAddress();
                if (StringUtils.isNotBlank(detailAddress)) {
                    activityAddress += detailAddress;
                }
            }
            // 类型
            String activityType = Optional.ofNullable(activityTypeEnum).map(Activity.ActivityTypeEnum::getName).orElse(StringUtils.isBlank(activityAddress) ? Activity.ActivityTypeEnum.ONLINE.getName() : Activity.ActivityTypeEnum.OFFLINE.getName());
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_type", "类型"), activityType, ++fieldFlag));

            // 地点
            fields.add(MhDataBuildUtil.buildField("地点", activityAddress, ++fieldFlag));
            // 主办方
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_organisers", "主办方"), record.getOrganisers(), ++fieldFlag));
            // 开始结束时间
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_time_scope", "活动时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()), ++fieldFlag));
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_time_scope", "活动时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()), ++fieldFlag));
            // 报名数据封装
            String emptySignUpText = urlParams.getString("emptySignUp");
            String signUpStatus = Optional.ofNullable(emptySignUpText).orElse(""), signUpStartTime = "", signUpEndTime = "";
            int signedUpNum = 0, personLimit = 0;
            if (signStat != null && CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
                if (signStat.getSignUpStartTime() != null && signStat.getSignUpEndTime() != null) {
                    signUpStatus = getSignUpStatus(signStat, urlParams);
                }
                if (signStat.getSignUpStartTime() != null) {
                    signUpStartTime = DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpStartTime());
                }
                if (signStat.getSignUpStartTime() != null) {
                    signUpEndTime = DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpEndTime());
                }
                signedUpNum = Optional.ofNullable(signStat.getSignedUpNum()).orElse(0);
                personLimit = Optional.ofNullable(signStat.getLimitNum()).orElse(0);
            }
            // 报名状态
            fields.add(MhDataBuildUtil.buildField("报名状态", signUpStatus, ++fieldFlag));
            // 已报名人数
            fields.add(MhDataBuildUtil.buildField("已报名人数", signedUpNum, ++fieldFlag));
            // 报名开始结束时间
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("sign_up_time_scope", "报名时间"), signUpStartTime, ++fieldFlag));
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("sign_up_time_scope", "报名时间"), signUpEndTime, ++fieldFlag));
            // 人数限制
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("sign_up_person_limit", "人数限制"), personLimit == 0 ? "不限" : signStat.getLimitNum(), ++fieldFlag));
            // 活动状态
            fields.add(MhDataBuildUtil.buildField("活动状态", Activity.getStatusDescription(record.getStatus()), ++fieldFlag));
            // 简介（40字纯文本）
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("introduction", "简介"), introductionMap.get(activityId), ++fieldFlag));
            // 活动分类
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("activity_classify", "分类"), record.getActivityClassifyName(), ++fieldFlag));
            // 标签
            fields.add(MhDataBuildUtil.buildField(fieldCodeNameMap.getOrDefault("tags", "标签"), record.getTags(), ++fieldFlag));
            // 自定义字段标题和值
            List<ActivityComponentValueDTO> componentValues = activityComponentValuesMap.get(activityId);
            if (CollectionUtils.isNotEmpty(componentValues)) {
                for (ActivityComponentValueDTO componentValue : componentValues) {
                    fields.add(MhDataBuildUtil.buildField(componentValue.getTemplateComponentName(), componentValue.getValue(), ++fieldFlag));

                }
            }
            // 模板的分类
            fields.add(MhDataBuildUtil.buildField("活动标识", record.getActivityFlag(), ++fieldFlag));
            fields.add(MhDataBuildUtil.buildField("发布时间", Optional.ofNullable(record.getReleaseTime()).map(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM::format).orElse(""), ++fieldFlag));
            fields.add(MhDataBuildUtil.buildField("typeID", record.getActivityClassifyId(), 102));
            fields.add(MhDataBuildUtil.buildField("活动时间段", DateUtils.activityTimeScope(record.getStartTime(), record.getEndTime(), DateUtils.MIDDLE_LINE_SEPARATOR), 103));
            Boolean needStatusValue = Optional.ofNullable(urlParams.getBoolean("needStatusValue")).orElse(false);
            if (needStatusValue) {
                fields.add(MhDataBuildUtil.buildField("活动状态value", getStatusValue(record), ++fieldFlag));
            }
            activityJsonArray.add(activity);
        }
        return activityJsonArray;
    }

    /**从signStat报名时间获取报名的进行状态
     * @Description
     * @author huxiaolong
     * @Date 2021-09-26 17:59:31
     * @param signStat
     * @return java.lang.String
     */
    private String getSignUpStatus(SignStatDTO signStat, JSONObject urlParams) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = signStat.getSignUpStartTime();
        LocalDateTime endTime = signStat.getSignUpEndTime();
        String customText;
        if (startTime.isAfter(now)) {
            customText = urlParams.getString("signUpNotStarted");
            return StringUtils.isNotBlank(customText)? customText : "报名未开始";
        }
        if (now.isAfter(endTime)) {
            customText = urlParams.getString("signUpEnded");
            return StringUtils.isNotBlank(customText)? customText : "已结束";
        }
        customText = urlParams.getString("signUpOngoing");
        return StringUtils.isNotBlank(customText)? customText : "报名中";
    }

    private Integer getStatusValue(Activity activity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = activity.getStartTime();
        LocalDateTime endTime = activity.getEndTime();
        if (startTime.isAfter(now)) {
            return 1;
        }
        if (now.isAfter(endTime)) {
            return 3;
        }
        return 2;
    }
}
