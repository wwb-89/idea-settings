package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.sign.SignIn;
import com.chaoxing.activity.dto.manager.sign.SignUp;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyHandleService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.util.FormUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**表单审批api服务
 * @author wwb
 * @version ver 1.0
 * @className FormApprovalApiService
 * @description
 * @blame wwb
 * @date 2021-05-10 17:46:00
 */
@Slf4j
@Service
public class FormApprovalApiService {

    /** 日期格式化 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
    /** sign */
    private static final String SIGN = "approveData_activity";
    /** key */
    private static final String KEY = "XtTpP2MjfoHZa^5!s8";
    /** 是 */
    private static final String YES = "是";
    /** 表单api域名 */
    private static final String FORM_API_DOMAIN = "http://m.oa.chaoxing.com";
    /** 获取表单数据url */
    private static final String GET_FORM_DATA_URL = FORM_API_DOMAIN + "/api/approve/forms/user/data/list";
    /** 获取表单数据列表 */
    private static final String LIST_FORM_DATA_URL = FORM_API_DOMAIN + "/api/approve/forms/advanced/search/list";

    @Resource
    private PassportApiService passportApiService;
    @Resource
    private ActivityClassifyHandleService activityClassifyHandleService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;
    @Resource
    private WebTemplateService webTemplateService;
    @Resource
    private SignApiService signApiService;

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    public FormDTO getFormData(Integer fid, Integer formId, Integer formUserId) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DATE_TIME_FORMATTER);
        // 参数
        TreeMap<String, Object> treeMap = Maps.newTreeMap();
        treeMap.put("deptId", fid);
        treeMap.put("formId", formId);
        treeMap.put("formUserIds", formUserId);
        treeMap.put("datetime", dateStr);
        treeMap.put("sign", SIGN);
        String enc = calGetFormDataEnc(treeMap);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(treeMap);
        params.add("enc", enc);
        String result = restTemplate.postForObject(GET_FORM_DATA_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Boolean success = jsonObject.getBoolean("success");
        success = Optional.ofNullable(success).orElse(Boolean.FALSE);
        if (success) {
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray data = jsonObject.getJSONArray("formUserList");
            return JSON.parseObject(data.getJSONObject(0).toJSONString(), FormDTO.class);
        } else {
            String errorMessage = jsonObject.getString("msg");
            log.error("获取机构:{}的表单:{}数据error:{}, url:{}, prams:{}", fid, formId, errorMessage, GET_FORM_DATA_URL, JSON.toJSONString(params));
            throw new BusinessException(errorMessage);
        }
    }

    private String calGetFormDataEnc(TreeMap<String, Object> params) {
        StringBuilder endBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            endBuilder.append("[");
            endBuilder.append(entry.getKey());
            endBuilder.append("=");
            endBuilder.append(entry.getValue());
            endBuilder.append("]");
        }
        endBuilder.append("[");
        endBuilder.append(KEY);
        endBuilder.append("]");
        return DigestUtils.md5Hex(endBuilder.toString());
    }

    public List<FormDTO> listFormData(Integer fid, Integer formId) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DATE_TIME_FORMATTER);
        // 参数
        TreeMap<String, Object> treeMap = Maps.newTreeMap();
        treeMap.put("deptId", fid);
        treeMap.put("formId", formId);
        treeMap.put("datetime", dateStr);
        treeMap.put("pageSize", 100);
        treeMap.put("sign", SIGN);
        String enc = calListFormDataEnc(treeMap);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(treeMap);
        params.add("enc", enc);
        String result = restTemplate.postForObject(LIST_FORM_DATA_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Boolean success = jsonObject.getBoolean("success");
        success = Optional.ofNullable(success).orElse(Boolean.FALSE);
        if (success) {
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray data = jsonObject.getJSONArray("data");
            return null;
        } else {
            String errorMessage = jsonObject.getString("msg");
            log.error("获取机构:{}的表单:{}数据error:{}, url:{}, prams:{}", fid, formId, errorMessage, GET_FORM_DATA_URL, JSON.toJSONString(params));
            throw new BusinessException(errorMessage);
        }
    }

    private String calListFormDataEnc(TreeMap<String, Object> params) {
        StringBuilder endBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            endBuilder.append("[");
            endBuilder.append(entry.getKey());
            endBuilder.append("=");
            endBuilder.append(entry.getValue());
            endBuilder.append("]");
        }
        endBuilder.append("[");
        endBuilder.append(KEY);
        endBuilder.append("]");
        return DigestUtils.md5Hex(endBuilder.toString());
    }

    /**创建活动
     * @Description 
     * @author wwb
     * @Date 2021-05-11 16:28:51
     * @param fid
     * @param formId
     * @param formUserId
     * @param templateId
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(Integer fid, Integer formId, Integer formUserId, String flag, Integer templateId) {
        // 获取表单数据
        FormDTO formData = getFormData(fid, formId, formUserId);
        if (!Objects.equals(formData.getAprvStatusTypeId(), CommonConstant.FORM_APPROVAL_AGREE_VALUE)) {
            // 审批不通过的忽略
            return;
        }
        // 根据表单数据创建活动
        Activity activity = buildActivityFromActivityApproval(formData);
        if (activity == null) {
            return;
        }
        // 根据表单数据创建报名签到
        SignAddEditDTO signAddEditDTO = buildSignFromActivityApproval(formData, activity.getCreateUid());
        // 设置活动标识
        Activity.ActivityFlagEnum activityFlag = Activity.ActivityFlagEnum.fromValue(flag);
        if (activityFlag == null) {
            activityFlag = Activity.ActivityFlagEnum.NORMAL;
        }
        activity.setActivityFlag(activityFlag.getValue());
        // 使用指定的模板
        templateId = Optional.ofNullable(templateId).orElse(CommonConstant.DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID);
        WebTemplate webTemplate = webTemplateService.getById(templateId);
        if (webTemplate == null) {
            throw new BusinessException("通过活动申报创建活动指定的门户模版不存在");
        }
        WfwRegionalArchitectureDTO wfwRegionalArchitecture = wfwRegionalArchitectureApiService.buildWfwRegionalArchitecture(fid);
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(activity.getCreateUid(), activity.getCreateUserName(), activity.getCreateFid(), activity.getCreateOrgName());
        activityHandleService.add(activity, signAddEditDTO, Lists.newArrayList(wfwRegionalArchitecture), loginUser);
        // 门户克隆模版
        activityHandleService.bindWebTemplate(activity.getId(), webTemplate.getId(), loginUser);
        // 发布
        activityHandleService.release(activity.getId(), loginUser);
    }

    /**获取需要创建的活动
     * @Description
     * @author wwb
     * @Date 2021-05-11 16:14:37
     * @param formData
     * @return com.chaoxing.activity.model.Activity
     */
    private Activity buildActivityFromActivityApproval(FormDTO formData) {
        Activity activity = Activity.buildDefault();
        Integer fid = formData.getFid();
        Integer formUserId = formData.getFormUserId();
        // 是否已经创建了活动，根据formUserId来判断
        Activity existActivity = activityQueryService.getByOriginTypeAndOrigin(Activity.OriginTypeEnum.ACTIVITY_DECLARATION, String.valueOf(formUserId));
        if (existActivity != null) {
            return null;
        }
        // 活动名称
        String activityName = FormUtils.getValue(formData, "activity_name");
        activity.setName(activityName);
        // 封面
        String coverCloudId = FormUtils.getCloudId(formData, "cover");
        if (StringUtils.isNotBlank(coverCloudId)) {
            activity.setCoverCloudId(coverCloudId);
        }
        // 开始时间、结束时间
        TimeScopeDTO activityTimeScope = FormUtils.getTimeScope(formData, "activity_time");
        activity.setStartTime(activityTimeScope.getStartTime());
        activity.setEndTime(activityTimeScope.getEndTime());
        // 活动分类
        String activityClassifyName = FormUtils.getValue(formData, "activity_classify");
        ActivityClassify activityClassify = activityClassifyHandleService.addAndGet(activityClassifyName, fid);
        activity.setActivityClassifyId(activityClassify.getId());
        // 积分
        String integralStr = FormUtils.getValue(formData, "integral_value");
        if (StringUtils.isNotBlank(integralStr)) {
            activity.setIntegralValue(BigDecimal.valueOf(Double.parseDouble(integralStr)));
        }
        String orgName = passportApiService.getOrgName(fid);
        // 主办方
        String organisers = FormUtils.getValue(formData, "organisers");
        if (StringUtils.isBlank(organisers)) {
            organisers = orgName;
        }
        activity.setOrganisers(organisers);
        // 是否开启评价
        String openRating = FormUtils.getValue(formData, "is_open_rating");
        activity.setOpenRating(Objects.equals(YES, openRating));
        // 活动类型
        String activityType = FormUtils.getValue(formData, "activity_type");
        if (StringUtils.isNotBlank(activityType)) {
            Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromName(activityType);
            if (activityTypeEnum != null) {
                activity.setActivityType(activityTypeEnum.getValue());
                AddressDTO activityAddress = FormUtils.getAddress(formData, "activity_address");
                String activityDetailAddress = FormUtils.getValue(formData, "activity_detail_address");
                activityDetailAddress = Optional.ofNullable(activityDetailAddress).orElse("");
                if (activityAddress != null) {
                    activity.setAddress(activityAddress.getAddress());
                    activity.setLongitude(activityAddress.getLng());
                    activity.setDimension(activityAddress.getLat());
                }
                activity.setDetailAddress(activityDetailAddress);
            }
        } else {
            activity.setActivityType(Activity.ActivityTypeEnum.ONLINE.getValue());
        }
        // 简介
        String introduction = FormUtils.getValue(formData, "introduction");
        introduction = Optional.ofNullable(introduction).orElse("");
        activity.setIntroduction(introduction);
        // 是否开启作品征集
        String openWork = FormUtils.getValue(formData, "is_open_work");
        activity.setOpenWork(Objects.equals(YES, openWork));
        // 学分
        String credit = FormUtils.getValue(formData, "credit");
        if (StringUtils.isNotBlank(credit)) {
            activity.setCredit(new BigDecimal(credit));
        }
        // 学时
        String period = FormUtils.getValue(formData, "period");
        if (StringUtils.isNotBlank(period)) {
            activity.setPeriod(new BigDecimal(period));
        }
        // 最大参与时长
        String timeLengthUpperLimitStr = FormUtils.getValue(formData, "time_length_upper_limit");
        if (StringUtils.isNotBlank(timeLengthUpperLimitStr)) {
            Integer timeLengthUpperLimit = Integer.parseInt(timeLengthUpperLimitStr);
            activity.setTimeLengthUpperLimit(timeLengthUpperLimit);
        }

        activity.setCreateUid(formData.getUid());
        activity.setCreateUserName(formData.getUname());
        activity.setCreateFid(fid);
        activity.setCreateOrgName(orgName);
        activity.setOriginType(Activity.OriginTypeEnum.ACTIVITY_DECLARATION.getValue());
        activity.setOrigin(String.valueOf(formUserId));
        return activity;
    }

    /**通过活动审批创建报名签到
     * @Description 
     * @author wwb
     * @Date 2021-06-11 17:49:43
     * @param formData
     * @param uid
     * @return com.chaoxing.activity.dto.module.SignAddEditDTO
    */
    private SignAddEditDTO buildSignFromActivityApproval(FormDTO formData, Integer uid) {
        SignAddEditDTO signAddEdit = SignAddEditDTO.buildDefault();
        // 报名
        List<SignUp> signUps = signAddEdit.getSignUps();
        SignUp signUp = signUps.get(0);
        String isOpenSignUp = FormUtils.getValue(formData, "is_open_sign_up");
        if (Objects.equals(YES, isOpenSignUp)) {
            signUp.setDeleted(false);
            String signUpOpenAudit = FormUtils.getValue(formData, "sign_up_open_audit");
            signUp.setOpenAudit(Objects.equals(YES, signUpOpenAudit));
            TimeScopeDTO signUpTimeScope = FormUtils.getTimeScope(formData, "sign_up_time");
            signUp.setStartTime(signUpTimeScope.getStartTime());
            signUp.setEndTime(signUpTimeScope.getEndTime());
            String signUpEndAllowCancel = FormUtils.getValue(formData, "sign_up_end_allow_cancel");
            signUp.setEndAllowCancel(Objects.equals(YES, signUpEndAllowCancel));
            String signUpPublicList = FormUtils.getValue(formData, "sign_up_public_list");
            signUp.setPublicList(Objects.equals(YES, signUpPublicList));
            String signUpPersonLimit = FormUtils.getValue(formData, "sign_up_person_limit");
            if (StringUtils.isNotBlank(signUpPersonLimit)) {
                signUp.setLimitPerson(true);
                signUp.setPersonLimit(Integer.parseInt(signUpPersonLimit));
            }
            // 报名填报信息 sign_up_fill_info
            List<String> fieldNames = FormUtils.listValue(formData, "sign_up_fill_info");
            if (CollectionUtils.isNotEmpty(fieldNames)) {
                // 创建表单
                Integer formId = signApiService.createFormBySystemFieldNames(fieldNames, uid);
                signUp.setFillInfo(true);
                signUp.setFillInfoFormId(formId);
            }

        } else {
            signUp.setDeleted(true);
        }

        List<SignIn> signIns = signAddEdit.getSignIns();
        // 签到
        SignIn signIn = signIns.get(0);
        String isOpenSignIn = FormUtils.getValue(formData, "is_open_sign_in");
        if (Objects.equals(YES, isOpenSignIn)) {
            signIn.setDeleted(false);
            String signInPublicList = FormUtils.getValue(formData, "sign_in_public_list");
            signIn.setPublicList(Objects.equals(YES, signInPublicList));
            TimeScopeDTO signInTimeScope = FormUtils.getTimeScope(formData, "sign_in_time");
            signIn.setStartTime(signInTimeScope.getStartTime());
            signIn.setEndTime(signInTimeScope.getEndTime());
            String signInWay = FormUtils.getValue(formData, "sign_in_way");
            SignIn.Way way = SignIn.Way.fromName(signInWay);
            if (way != null) {
                signIn.setWay(way.getValue());
                if (Objects.equals(SignIn.Way.POSITION, way)) {
                    AddressDTO signInAddress = FormUtils.getAddress(formData, "sign_in_address");
                    if (signInAddress != null) {
                        signIn.setAddress(signInAddress.getAddress());
                        signIn.setLongitude(signInAddress.getLng());
                        signIn.setDimension(signInAddress.getLat());
                    }
                }
            } else {
                signIn.setWay(SignIn.Way.QR_CODE.getValue());
                SignIn.ScanCodeWay scanCodeWay = SignIn.ScanCodeWay.fromName(signInWay);
                if (scanCodeWay != null) {
                    signIn.setScanCodeWay(scanCodeWay.getValue());
                } else {
                    signIn.setScanCodeWay(SignIn.ScanCodeWay.PARTICIPATOR.getValue());
                }
            }
        } else {
            signIn.setDeleted(true);
        }
        // 签退
        SignIn signOut = signIns.get(1);
        String isOpenSignOut = FormUtils.getValue(formData, "is_open_sign_out");
        if (Objects.equals(YES, isOpenSignOut)) {
            signOut.setDeleted(false);
            String signOutPublicList = FormUtils.getValue(formData, "sign_out_public_list");
            signOut.setPublicList(Objects.equals(YES, signOutPublicList));
            TimeScopeDTO signOutTimeScope = FormUtils.getTimeScope(formData, "sign_out_time");
            signOut.setStartTime(signOutTimeScope.getStartTime());
            signOut.setEndTime(signOutTimeScope.getEndTime());
            String signOutWay = FormUtils.getValue(formData, "sign_out_way");
            SignIn.Way way = SignIn.Way.fromName(signOutWay);
            if (way != null) {
                signOut.setWay(way.getValue());
                if (Objects.equals(SignIn.Way.POSITION, way)) {
                    AddressDTO signOutAddress = FormUtils.getAddress(formData, "sign_out_address");
                    if (signOutAddress != null) {
                        signOut.setAddress(signOutAddress.getAddress());
                        signOut.setLongitude(signOutAddress.getLng());
                        signOut.setDimension(signOutAddress.getLat());
                    }
                }
            } else {
                signOut.setWay(SignIn.Way.QR_CODE.getValue());
                SignIn.ScanCodeWay scanCodeWay = SignIn.ScanCodeWay.fromName(signOutWay);
                if (scanCodeWay != null) {
                    signOut.setScanCodeWay(scanCodeWay.getValue());
                } else {
                    signOut.setScanCodeWay(SignIn.ScanCodeWay.PARTICIPATOR.getValue());
                }
            }
        } else {
            signOut.setDeleted(true);
        }
        return signAddEdit;
    }

}