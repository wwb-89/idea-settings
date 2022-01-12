package com.chaoxing.activity.service.queue.user.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushConfig;
import com.chaoxing.activity.model.UserDataPushRecord;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.v2.DataPushConfigService;
import com.chaoxing.activity.service.data.v2.MarketUserDataPushRecordService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.queue.user.UserDataPushQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import com.chaoxing.activity.util.CalculateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserDataPushQueueService
 * @description
 * @blame wwb
 * @date 2021-11-02 15:41:26
 */
@Slf4j
@Service
public class UserDataPushQueueService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Resource
    private DataPushConfigService dataPushConfigService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private WfwFormApiService wfwFormApiService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private UserResultQueryService userResultQueryService;
    @Resource
    private MarketUserDataPushRecordService marketUserDataPushRecordService;

    public void handle(UserDataPushQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        Integer uid = queueParam.getUid();
        Integer activityId = queueParam.getActivityId();
        Integer dataPushConfigId = queueParam.getDataPushConfigId();
        DataPushConfig dataPushConfig = dataPushConfigService.getById(dataPushConfigId);
        if (dataPushConfig == null) {
            return;
        }
        UserStatSummary userStatSummary = userStatSummaryQueryService.getByUidAndActivityId(uid, activityId);
        if (userStatSummary == null) {
            return;
        }
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        DataPushConfig.WayEnum way = DataPushConfig.WayEnum.fromValue(dataPushConfig.getWay());
        switch (way) {
            case WFW_FORM:
                handleWfwFormDataPush(activity, userStatSummary, dataPushConfig);
                break;
            case URL:
                handleUrlDataPush();
                break;
            default:
        }
    }

    private void handleWfwFormDataPush(Activity activity, UserStatSummary userStatSummary, DataPushConfig dataPushConfig) {
        // 表单id
        Integer formId = Optional.ofNullable(dataPushConfig.getWayValue()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null);
        if (formId == null) {
            return;
        }
        Integer uid = userStatSummary.getUid();
        Integer configId = dataPushConfig.getId();
        Integer activityId = activity.getId();
        Integer formFid = dataPushConfig.getFid();
        Integer formUserId = null;
        // 查询已经推送的记录
        UserDataPushRecord userDataPushRecord = marketUserDataPushRecordService.get(uid, activityId, configId);
        if (userDataPushRecord != null) {
            formUserId = Optional.ofNullable(userDataPushRecord.getTargetIdentify()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null);
            if (formUserId != null) {
                FormDataDTO formRecord = wfwFormApiService.getFormRecord(formUserId, formId, formFid);
                if (formRecord == null) {
                    // 表单中数据不存在
                    formUserId = null;
                }
            }
            if (formUserId == null) {
                marketUserDataPushRecordService.delete(userDataPushRecord.getId());
            }
        }
        String wfwFormData = generateWfwFormPushData(activity, userStatSummary, dataPushConfig);
        if (formUserId == null) {
            // 新增
            formUserId = wfwFormApiService.fillForm(formId, formFid, activity.getCreateUid(), wfwFormData);
        } else {
            // 更新
            wfwFormApiService.updateForm(formId, formUserId, wfwFormData);
        }
        marketUserDataPushRecordService.addOrUpdate(UserDataPushRecord.builder()
                .uid(uid)
                .activityId(activityId)
                .configId(configId)
                .marketId(activity.getMarketId())
                .targetIdentify(String.valueOf(formUserId))
                .build());
    }

    private String generateWfwFormPushData(Activity activity, UserStatSummary userStatSummary, DataPushConfig dataPushConfig) {
        Integer activityId = activity.getId();
        if (userStatSummary == null) {
            return null;
        }
        JSONArray result = new JSONArray();
        Integer formId = Integer.parseInt(dataPushConfig.getWayValue());
        Integer formFid = dataPushConfig.getFid();
        List<FormStructureDTO> formInfos = wfwFormApiService.getFormStructure(formId, formFid);
        if (CollectionUtils.isNotEmpty(formInfos)) {
            List<String> handledAlias = Lists.newArrayList();
            for (FormStructureDTO formInfo : formInfos) {
                String alias = formInfo.getAlias();
                if (handledAlias.contains(alias)) {
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("compt", formInfo.getCompt());
                item.put("comptId", formInfo.getId());
                item.put("alias", alias);
                JSONArray data = new JSONArray();
                if (Objects.equals(alias, "user")) {
                    JSONObject user = new JSONObject();
                    user.put("id", userStatSummary.getUid());
                    user.put("name", userStatSummary.getRealName());
                    data.add(user);
                    item.put("idNames", data);
                    result.add(item);
                } else if (Objects.equals(alias, "uname")) {
                    data.add(userStatSummary.getUname());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "department")) {
                    // 部门信息
                    if (Objects.equals("department", formInfo.getCompt())) {
                        List<WfwDepartmentDTO> wfwDepartments = wfwContactApiService.listUserJoinDepartment(userStatSummary.getUid(), activity.getCreateFid());
                        if (CollectionUtils.isNotEmpty(wfwDepartments)) {
                            for (WfwDepartmentDTO wfwDepartment : wfwDepartments) {
                                JSONObject user = new JSONObject();
                                user.put("id", wfwDepartment.getId());
                                user.put("name", wfwDepartment.getName());
                                data.add(user);
                            }
                            item.put("idNames", data);
                            result.add(item);
                        }
                    } else {
                        data.add(userStatSummary.getOrganizationStructure());
                        item.put("val", data);
                        result.add(item);
                    }
                } else if (Objects.equals(alias, "activity_id")) {
                    data.add(userStatSummary.getActivityId());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "activity_name")) {
                    data.add(activity.getName());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "activity_classify")) {
                    data.add(activity.getActivityClassifyName());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "sign_up_status")) {
                    Integer signUpNum = Optional.ofNullable(userStatSummary.getSignUpNum()).orElse(0);
                    Integer signedUpNum = Optional.ofNullable(userStatSummary.getSignedUpNum()).orElse(0);
                    String signUpStatue = "-";
                    if (signUpNum > 0) {
                        if (signedUpNum > 0) {
                            signUpStatue = "已报名";
                        } else {
                            signUpStatue = "未报名";
                        }
                    }
                    data.add(signUpStatue);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "signed_in_time")) {
                    data.add(userStatSummary.getSignedInNum());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "sign_in_leave_time")) {
                    data.add(userStatSummary.getSignInLeaveNum());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "un_signed_in_time")) {
                    data.add(userStatSummary.getNotSignInNum());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "sign_in_rate")) {
                    // 签到率
                    String signInRateStr = "";
                    BigDecimal signInRate = userStatSummary.getSignedInRate();
                    if (signInRate != null && signInRate.compareTo(BigDecimal.valueOf(0)) >= 0) {
                        signInRateStr = CalculateUtils.mul(signInRate.doubleValue(), 100) + "%";
                    }
                    data.add(signInRateStr);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "rating_status")) {
                    Integer ratingNum = Optional.ofNullable(userStatSummary.getRatingNum()).orElse(0);
                    data.add(ratingNum > 0 ? "已评价" : "未评价");
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "integral")) {
                    data.add(userStatSummary.getActivityIntegral());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "unit")) {
                    data.add("积分");
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "get_time")) {
                    LocalDateTime auditTime = userStatSummary.getCreateTime();
                    if (auditTime != null) {
                        data.add(auditTime.format(DATE_TIME_FORMATTER));
                        item.put("val", data);
                        result.add(item);
                    }
                } else if (Objects.equals(alias, "organizer_audit_status")) {
                    data.add(userResultQueryService.getResultQualifiedDescription(userStatSummary.getUid(), activityId));
                    item.put("val", data);
                    result.add(item);
                }  else if (Objects.equals(alias, "integral_no")) {
                    String integralNo = userStatSummary.getUid() + "" + activityId;
                    data.add(integralNo);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "participate_in_length")) {
                    Integer participateTimeLength = Optional.ofNullable(userStatSummary.getParticipateTimeLength()).orElse(0);
                    data.add(participateTimeLength);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "participate_in_hour_length")) {
                    Integer participateTimeLength = Optional.ofNullable(userStatSummary.getParticipateTimeLength()).orElse(0);
                    data.add(Math.round(CalculateUtils.div(participateTimeLength, 60)));
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "activity_time_scope")) {
                    data.add(activity.getStartTime().format(DATE_TIME_FORMATTER));
                    data.add(activity.getEndTime().format(DATE_TIME_FORMATTER));
                    item.put("val", data);
                    result.add(item);
                }
                handledAlias.add(alias);
            }
        }
        return result.toJSONString();
    }

    private void handleUrlDataPush() {

    }

}
