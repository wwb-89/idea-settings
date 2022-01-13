package com.chaoxing.activity.service.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.model.OrgUserDataPushRecord;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
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

/**机构用户数据表单推送服务
 * @author wwb
 * @version ver 1.0
 * @className UserActivityDataFormPushService
 * @description
 * @blame wwb
 * @date 2021-06-24 19:11:49
 */
@Slf4j
@Service
public class OrgUserDataFormPushService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Integer ZJ_BUSINESS_COLLEGE_FID = 177443;
    private static final Integer ZJ_BUSINESS_COLLEGE_FORM_ID = 21341;
    private static final Integer ZJ_BUSINESS_CUSTOM_COMPONENT_ID = 10066;

    @Resource
    private WfwFormApiService wfwFormApiService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;
    @Resource
    private DataPushValidationService dataPushValidationService;
    @Resource
    private OrgUserDataPushRecordService orgUserDataPushRecordService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;
    @Resource
    private UserResultQueryService userResultQueryService;
    @Resource
    private ActivityComponentValueService activityComponentValueService;

    public void push(Integer uid, Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer fid = activity.getCreateFid();
        if (!dataPushValidationService.pushAble(fid, activity.getMarketId())) {
            // 是否允许推送
            return;
        }
        OrgDataRepoConfigDetail.RepoTypeEnum repoType = OrgDataRepoConfigDetail.RepoTypeEnum.FORM;
        OrgDataRepoConfigDetail.DataTypeEnum dataType = OrgDataRepoConfigDetail.DataTypeEnum.USER_ACTIVITY_DATA;
        OrgDataRepoConfigDetail orgConfigDetail = orgDataRepoConfigQueryService.getOrgConfigDetail(fid, dataType, repoType);
        if (orgConfigDetail == null) {
            return;
        }
        String repo = orgConfigDetail.getRepo();
        if (StringUtils.isBlank(repo)) {
            return;
        }
        Integer formId = Integer.parseInt(repo);
        Integer formUserId = null;
        Integer formFid = fid;
        boolean isSpecialFlag = Objects.equals(fid, ZJ_BUSINESS_COLLEGE_FID) && Objects.equals(formId, ZJ_BUSINESS_COLLEGE_FORM_ID);
        if (isSpecialFlag) {
            formFid = 1183;
        }
        OrgUserDataPushRecord orgUserDataPushRecord = orgUserDataPushRecordService.get(uid, activityId);
        if (orgUserDataPushRecord != null) {
            // 新增
            formUserId = orgUserDataPushRecord.getFormUserId();
            FormDataDTO formRecord = wfwFormApiService.getFormRecord(formUserId, formId, formFid);
            if (formRecord == null) {
                formUserId = null;
            }
        }
        UserStatSummary userStatSummary = userStatSummaryQueryService.getByUidAndActivityId(uid, activityId);
        String wfwFormData;
        boolean needPush = true;
        // 浙商只推送签到率>=50%的
        if (isSpecialFlag) {
            BigDecimal signedInRate = Optional.ofNullable(userStatSummary.getSignedInRate()).orElse(BigDecimal.ZERO);
            needPush = signedInRate.compareTo(BigDecimal.valueOf(0.5)) >= 0;
            wfwFormData = generateZjBusinessCollegeFormData(userStatSummary, activity.getName(), formId, formFid);
        } else {
            wfwFormData = generateWfwFormData(activity, userStatSummary, formId, formFid);
        }
        if (StringUtils.isBlank(wfwFormData)) {
            return;
        }
        if (formUserId == null && needPush) {
            // 新增
            formUserId = wfwFormApiService.fillForm(formId, formFid, uid, wfwFormData);
            orgUserDataPushRecord = OrgUserDataPushRecord.builder()
                    .uid(uid)
                    .activityId(activityId)
                    .formId(formId)
                    .formUserId(formUserId)
                    .build();
            orgUserDataPushRecordService.addOrUpdate(orgUserDataPushRecord);
        } else {
            if (needPush) {
                // 修改
                wfwFormApiService.updateForm(formId, formUserId, wfwFormData);
            } else {
                // 删除
                wfwFormApiService.deleteFormRecord(formUserId, formId);
            }
        }
    }

    private String generateWfwFormData(Activity activity, UserStatSummary userStatSummary, Integer formId, Integer fid) {
        Integer activityId = activity.getId();
        if (userStatSummary == null) {
            return null;
        }
        JSONArray result = new JSONArray();
        List<FormStructureDTO> formInfos = wfwFormApiService.getFormStructure(formId, fid);
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
                        WfwDepartmentDTO wfwDepartment = wfwContactApiService.getUserDepartment(userStatSummary.getUid(), activity.getCreateFid());
                        if (wfwDepartment != null) {
                            JSONObject user = new JSONObject();
                            user.put("id", wfwDepartment.getId());
                            user.put("name", wfwDepartment.getName());
                            data.add(user);
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
                    Integer ratingNum = userStatSummary.getRatingNum();
                    data.add(ratingNum == null ? "未评价" : "已评价");
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "integral")) {
                    BigDecimal integral = Optional.ofNullable(userStatSummary.getCorrectedIntegral()).orElse(userStatSummary.getActivityIntegral());
                    data.add(integral);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "unit")) {
                    data.add("积分");
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "period")) {
                    BigDecimal period = Optional.ofNullable(userStatSummary.getCorrectedPeriod()).orElse(userStatSummary.getPeriod());
                    data.add(period);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "credit")) {
                    BigDecimal credit = Optional.ofNullable(userStatSummary.getCorrectedCredit()).orElse(userStatSummary.getCredit());
                    data.add(credit);
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

    /**获取浙江商业技术学院表单数据
     * @Description 
     * @author wwb
     * @Date 2021-11-03 10:43:19
     * @param userStatSummary
     * @param activityName
     * @param formId
     * @param fid
     * @return java.lang.String
    */
    private String generateZjBusinessCollegeFormData(UserStatSummary userStatSummary, String activityName, Integer formId, Integer fid) {
        if (userStatSummary == null) {
            return null;
        }
        JSONArray result = new JSONArray();
        List<FormStructureDTO> formInfos = wfwFormApiService.getFormStructure(formId, fid);
        // 自定义组件数据
        Integer activityId = userStatSummary.getActivityId();
        String activityComponentValue = activityComponentValueService.getActivityComponentValue(activityId, ZJ_BUSINESS_CUSTOM_COMPONENT_ID);
        if (CollectionUtils.isNotEmpty(formInfos)) {
            for (FormStructureDTO formInfo : formInfos) {
                String alias = formInfo.getAlias();
                JSONObject item = new JSONObject();
                item.put("compt", formInfo.getCompt());
                item.put("comptId", formInfo.getId());
                item.put("alias", alias);
                JSONArray data = new JSONArray();
                if (Objects.equals(alias, "real_name")) {
                    data.add(userStatSummary.getRealName());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "mobile")) {
                    data.add(userStatSummary.getMobile());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "department")) {
                    data.add(userStatSummary.getOrganizationStructure());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "uid")) {
                    data.add(userStatSummary.getUid());
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "get_time")) {
                    LocalDateTime auditTime = userStatSummary.getCreateTime();
                    if (auditTime != null) {
                        data.add(auditTime.format(DATE_TIME_FORMATTER));
                        item.put("val", data);
                        result.add(item);
                    }
                } else if (Objects.equals(alias, "behavior")) {
                    data.add(activityComponentValue);
                    item.put("val", data);
                    result.add(item);
                } else if (Objects.equals(alias, "remark")) {
                    data.add(activityName);
                    item.put("val", data);
                    result.add(item);
                }
            }
        }
        return result.toJSONString();
    }

}