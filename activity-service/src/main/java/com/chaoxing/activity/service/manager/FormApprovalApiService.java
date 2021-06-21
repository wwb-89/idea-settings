package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
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
import com.chaoxing.activity.service.queue.FormActivityCreateQueueService;
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

    private static final DateTimeFormatter DATA_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** 日期格式化 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
    /** sign */
    private static final String SIGN = "approveData_activity";
    /** key */
    private static final String KEY = "XtTpP2MjfoHZa^5!s8";
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
    private FormActivityCreateQueueService formActivityCreateQueueService;
    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;
    @Resource
    private WebTemplateService webTemplateService;
    @Resource
    private MhApiService mhApiService;

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
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(Integer fid, Integer formId, Integer formUserId, String flag) {
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
        SignAddEditDTO signAddEditDTO = buildSignFromActivityApproval(formData);
        // 设置活动标识
        Activity.ActivityFlag activityFlag = Activity.ActivityFlag.fromValue(flag);
        if (activityFlag == null) {
            activityFlag = Activity.ActivityFlag.NORMAL;
        }
        activity.setActivityFlag(activityFlag.getValue());
        // 使用指定的模板
        WebTemplate webTemplate = webTemplateService.getById(CommonConstant.DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID);
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
            activity.setOpenIntegral(true);
            activity.setIntegralValue(BigDecimal.valueOf(Double.parseDouble(integralStr)));
        }
        // 主办方
        String organisers = FormUtils.getValue(formData, "organisers");
        activity.setOrganisers(organisers);
        // 是否开启评价
        String openRating = FormUtils.getValue(formData, "is_open_rating");
        activity.setOpenRating(Objects.equals("是", openRating));
        // 活动类型
        String activityType = FormUtils.getValue(formData, "activity_type");
        if (StringUtils.isNotBlank(activityType)) {
            Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromName(activityType);
            if (activityTypeEnum != null) {
                activity.setActivityType(activityTypeEnum.getValue());
                AddressDTO activity_address = FormUtils.getAddress(formData, "activity_address");
                if (activity_address != null) {
                    activity.setAddress(activity_address.getAddress());
                    activity.setLongitude(activity_address.getLng());
                    activity.setDimension(activity_address.getLat());
                }
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
        activity.setOpenWork(Objects.equals("是", openWork));
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

        activity.setCreateUid(formData.getUid());
        activity.setCreateUserName(formData.getUname());
        activity.setCreateFid(fid);
        String orgName = passportApiService.getOrgName(fid);
        activity.setCreateOrgName(orgName);
        activity.setOrganisers(orgName);
        activity.setOriginType(Activity.OriginTypeEnum.ACTIVITY_DECLARATION.getValue());
        activity.setOrigin(String.valueOf(formUserId));
        return activity;
    }

    /**通过活动审批创建报名签到
     * @Description 
     * @author wwb
     * @Date 2021-06-11 17:49:43
     * @param formData
     * @return com.chaoxing.activity.dto.module.SignAddEditDTO
    */
    private SignAddEditDTO buildSignFromActivityApproval(FormDTO formData) {
        SignAddEditDTO signAddEdit = SignAddEditDTO.buildDefault();
        // 报名
        List<SignUp> signUps = signAddEdit.getSignUps();
        SignUp signUp = signUps.get(0);
        String is_open_sign_up = FormUtils.getValue(formData, "is_open_sign_up");
        if (Objects.equals("是", is_open_sign_up)) {
            signUp.setDeleted(false);
            String sign_up_open_audit = FormUtils.getValue(formData, "sign_up_open_audit");
            signUp.setOpenAudit(Objects.equals("是", sign_up_open_audit));
            TimeScopeDTO signUpTimeScope = FormUtils.getTimeScope(formData, "sign_up_time");
            signUp.setStartTime(signUpTimeScope.getStartTime());
            signUp.setEndTime(signUpTimeScope.getEndTime());
            String sign_up_end_allow_cancel = FormUtils.getValue(formData, "sign_up_end_allow_cancel");
            signUp.setEndAllowCancel(Objects.equals("是", sign_up_end_allow_cancel));
            String sign_up_public_list = FormUtils.getValue(formData, "sign_up_public_list");
            signUp.setPublicList(Objects.equals("是", sign_up_public_list));
            String sign_up_person_limit = FormUtils.getValue(formData, "sign_up_person_limit");
            if (StringUtils.isNotBlank(sign_up_person_limit)) {
                signUp.setLimitPerson(true);
                signUp.setPersonLimit(Integer.parseInt(sign_up_person_limit));
            }
        } else {
            signUp.setDeleted(true);
        }

        List<SignIn> signIns = signAddEdit.getSignIns();
        // 签到
        SignIn signIn = signIns.get(0);
        String is_open_sign_in = FormUtils.getValue(formData, "is_open_sign_in");
        if (Objects.equals("是", is_open_sign_in)) {
            signIn.setDeleted(false);
            String signInPublicList = FormUtils.getValue(formData, "sign_in_public_list");
            signIn.setPublicList(Objects.equals("是", signInPublicList));
            TimeScopeDTO signInTimeScope = FormUtils.getTimeScope(formData, "sign_in_time");
            signIn.setStartTime(signInTimeScope.getStartTime());
            signIn.setEndTime(signInTimeScope.getEndTime());
            String sign_in_way = FormUtils.getValue(formData, "sign_in_way");
            SignIn.Way way = SignIn.Way.fromName(sign_in_way);
            if (way != null) {
                signIn.setWay(way.getValue());
                if (Objects.equals(SignIn.Way.POSITION, way)) {
                    AddressDTO sign_in_address = FormUtils.getAddress(formData, "sign_in_address");
                    if (sign_in_address != null) {
                        signIn.setAddress(sign_in_address.getAddress());
                        signIn.setLongitude(sign_in_address.getLng());
                        signIn.setDimension(sign_in_address.getLat());
                    }
                }
            } else {
                signIn.setWay(SignIn.Way.QR_CODE.getValue());
                SignIn.ScanCodeWay scanCodeWay = SignIn.ScanCodeWay.fromName(sign_in_way);
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
        String is_open_sign_out = FormUtils.getValue(formData, "is_open_sign_out");
        if (Objects.equals("是", is_open_sign_out)) {
            signOut.setDeleted(false);
            String sign_out_public_list = FormUtils.getValue(formData, "sign_out_public_list");
            signOut.setPublicList(Objects.equals("是", sign_out_public_list));
            TimeScopeDTO signOutTimeScope = FormUtils.getTimeScope(formData, "sign_out_time");
            signOut.setStartTime(signOutTimeScope.getStartTime());
            signOut.setEndTime(signOutTimeScope.getEndTime());
            String sign_out_way = FormUtils.getValue(formData, "sign_out_way");
            SignIn.Way way = SignIn.Way.fromName(sign_out_way);
            if (way != null) {
                signOut.setWay(way.getValue());
                if (Objects.equals(SignIn.Way.POSITION, way)) {
                    AddressDTO sign_out_address = FormUtils.getAddress(formData, "sign_out_address");
                    if (sign_out_address != null) {
                        signOut.setAddress(sign_out_address.getAddress());
                        signOut.setLongitude(sign_out_address.getLng());
                        signOut.setDimension(sign_out_address.getLat());
                    }
                }
            } else {
                signOut.setWay(SignIn.Way.QR_CODE.getValue());
                SignIn.ScanCodeWay scanCodeWay = SignIn.ScanCodeWay.fromName(sign_out_way);
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