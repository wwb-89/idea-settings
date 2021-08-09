package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignInCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.util.FormUtils;
import com.chaoxing.activity.util.DateUtils;
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
import java.util.stream.Collectors;

/**微服务表单审批api服务
 * @author wwb
 * @version ver 1.0
 * @className FormApprovalApiService
 * @description
 * @blame wwb
 * @date 2021-05-10 17:46:00
 */
@Slf4j
@Service
public class WfwFormApprovalApiService {

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
    private ClassifyHandleService classifyHandleService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private WfwAreaApiService wfwAreaApiService;
    @Resource
    private WebTemplateService webTemplateService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private TemplateQueryService templateQueryService;

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    public WfwFormDTO getFormData(Integer fid, Integer formId, Integer formUserId) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DATE_TIME_FORMATTER);
        // 参数
        TreeMap<String, Object> treeMap = Maps.newTreeMap();
        treeMap.put("deptId", fid);
        treeMap.put("formId", formId);
        treeMap.put("formUserIds", formUserId);
        treeMap.put("datetime", dateStr);
        treeMap.put("sign", SIGN);
        treeMap.put("enc", getEnc(treeMap));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(treeMap);
        String result = restTemplate.postForObject(GET_FORM_DATA_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Boolean success = jsonObject.getBoolean("success");
        success = Optional.ofNullable(success).orElse(Boolean.FALSE);
        if (success) {
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray data = jsonObject.getJSONArray("formUserList");
            if (data.size() < 1) {
                return null;
            }
            return JSON.parseObject(data.getJSONObject(0).toJSONString(), WfwFormDTO.class);
        } else {
            String errorMessage = jsonObject.getString("msg");
            log.error("获取机构:{}的表单:{}数据error:{}, url:{}, prams:{}", fid, formId, errorMessage, GET_FORM_DATA_URL, JSON.toJSONString(params));
            throw new BusinessException(errorMessage);
        }
    }

    private String getEnc(TreeMap<String, Object> params) {
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

    public List<WfwFormDTO> listFormData(Integer fid, Integer formId) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DATE_TIME_FORMATTER);
        // 参数
        TreeMap<String, Object> treeMap = Maps.newTreeMap();
        treeMap.put("deptId", fid);
        treeMap.put("formId", formId);
        treeMap.put("datetime", dateStr);
        treeMap.put("pageSize", 100);
        treeMap.put("sign", SIGN);
        treeMap.put("enc", getEnc(treeMap));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(treeMap);
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

    /**创建活动
     * @Description 
     * @author wwb
     * @Date 2021-05-11 16:28:51
     * @param fid
     * @param formId
     * @param formUserId
     * @param marketId
     * @param flag
     * @param webTemplateId 门户网页模版id
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(Integer fid, Integer formId, Integer formUserId, Integer marketId, String flag, Integer webTemplateId) {
        // 获取表单数据
        WfwFormDTO formData = getFormData(fid, formId, formUserId);
        if (formData == null) {
            return;
        }
        if (!Objects.equals(formData.getAprvStatusTypeId(), CommonConstant.FORM_APPROVAL_AGREE_VALUE)) {
            // 审批不通过的忽略
            return;
        }
        // 根据表单数据创建活动
        ActivityCreateParamDTO activity = buildActivityFromActivityApproval(formData);
        if (activity == null) {
            return;
        }
        LoginUserDTO loginUser = activity.getLoginUser();
        // 根据表单数据创建报名签到
        SignCreateParamDTO signCreateParam = buildSignFromActivityApproval(formData, loginUser.getUid());
        // 设置活动标识
        Activity.ActivityFlagEnum activityFlag = Activity.ActivityFlagEnum.fromValue(flag);
        if (activityFlag == null) {
            activityFlag = Activity.ActivityFlagEnum.NORMAL;
        }
        // 使用指定的模板
        webTemplateId = Optional.ofNullable(webTemplateId).orElse(CommonConstant.DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID);
        WebTemplate webTemplate = webTemplateService.getById(webTemplateId);
        if (webTemplate == null) {
            throw new BusinessException("通过活动申报创建活动指定的门户模版不存在");
        }
        activity.setWebTemplateId(webTemplateId);
        // 设置活动市场
        if (marketId != null) {
            List<Market> markets = marketQueryService.listByFid(fid);
            if (CollectionUtils.isEmpty(markets)) {
                log.error("机构:{} 活动市场不存在", fid);
                return;
            }
            if (!markets.stream().map(Market::getId).collect(Collectors.toList()).contains(marketId)) {
                log.error("机构:{} 活动市场:{} 不存在", fid, marketId);
                return;
            }
            activity.setMarketId(marketId);
            Template template = templateQueryService.getMarketFirstTemplate(marketId);
            if (template == null) {
                log.error("活动市场:{}下不存在模版", marketId);
                return;
            }
            activity.setTemplateId(template.getId());
        }
        WfwAreaDTO wfwRegionalArchitecture = wfwAreaApiService.buildWfwRegionalArchitecture(fid);
        Integer activityId = activityHandleService.add(activity, signCreateParam, Lists.newArrayList(wfwRegionalArchitecture), loginUser);
        // 发布
        activityHandleService.release(activityId, loginUser);
    }

    /**获取需要创建的活动
     * @Description
     * @author wwb
     * @Date 2021-05-11 16:14:37
     * @param formData
     * @return com.chaoxing.activity.dto.activity.ActivityCreateParamDTO
     */
    private ActivityCreateParamDTO buildActivityFromActivityApproval(WfwFormDTO formData) {
        ActivityCreateParamDTO activityCreateParamDto = ActivityCreateParamDTO.buildDefault();
        Integer fid = formData.getFid();
        Integer formUserId = formData.getFormUserId();
        // 是否已经创建了活动，根据formUserId来判断
        Activity existActivity = activityQueryService.getByOriginTypeAndOrigin(Activity.OriginTypeEnum.ACTIVITY_DECLARATION, String.valueOf(formUserId));
        if (existActivity != null) {
            return null;
        }
        // 活动名称
        String activityName = FormUtils.getValue(formData, "activity_name");
        activityCreateParamDto.setName(activityName);
        // 封面
        String coverCloudId = FormUtils.getCloudId(formData, "cover");
        if (StringUtils.isNotBlank(coverCloudId)) {
            activityCreateParamDto.setCoverCloudId(coverCloudId);
        }
        // 开始时间、结束时间
        TimeScopeDTO activityTimeScope = FormUtils.getTimeScope(formData, "activity_time");
        activityCreateParamDto.setStartTimeStamp(DateUtils.date2Timestamp(activityTimeScope.getStartTime()));
        activityCreateParamDto.setEndTimeStamp(DateUtils.date2Timestamp(activityTimeScope.getEndTime()));
        // 活动分类
        String activityClassifyName = FormUtils.getValue(formData, "activity_classify");
        Classify classify = classifyHandleService.getOrAddOrgClassify(fid, activityClassifyName);
        activityCreateParamDto.setActivityClassifyId(classify.getId());
        // 积分
        String integralStr = FormUtils.getValue(formData, "integral_value");
        if (StringUtils.isNotBlank(integralStr)) {
            activityCreateParamDto.setIntegral(BigDecimal.valueOf(Double.parseDouble(integralStr)));
        }
        String orgName = passportApiService.getOrgName(fid);
        // 主办方
        String organisers = FormUtils.getValue(formData, "organisers");
        if (StringUtils.isBlank(organisers)) {
            organisers = orgName;
        }
        activityCreateParamDto.setOrganisers(organisers);
        // 是否开启评价
        String openRating = FormUtils.getValue(formData, "is_open_rating");
        activityCreateParamDto.setOpenRating(Objects.equals(YES, openRating));
        // 活动类型
        String activityType = FormUtils.getValue(formData, "activity_type");
        if (StringUtils.isNotBlank(activityType)) {
            Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromName(activityType);
            if (activityTypeEnum != null) {
                activityCreateParamDto.setActivityType(activityTypeEnum.getValue());
                AddressDTO activityAddress = FormUtils.getAddress(formData, "activity_address");
                String activityDetailAddress = FormUtils.getValue(formData, "activity_detail_address");
                activityDetailAddress = Optional.ofNullable(activityDetailAddress).orElse("");
                if (activityAddress != null) {
                    activityCreateParamDto.setAddress(activityAddress.getAddress());
                    activityCreateParamDto.setLongitude(activityAddress.getLng());
                    activityCreateParamDto.setDimension(activityAddress.getLat());
                }
                activityCreateParamDto.setDetailAddress(activityDetailAddress);
            }
        } else {
            activityCreateParamDto.setActivityType(Activity.ActivityTypeEnum.ONLINE.getValue());
        }
        // 简介
        String introduction = FormUtils.getValue(formData, "introduction");
        introduction = Optional.ofNullable(introduction).orElse("");
        activityCreateParamDto.setIntroduction(introduction);
        // 是否开启作品征集
        String openWork = FormUtils.getValue(formData, "is_open_work");
        activityCreateParamDto.setOpenWork(Objects.equals(YES, openWork));
        // 学分
        String credit = FormUtils.getValue(formData, "credit");
        if (StringUtils.isNotBlank(credit)) {
            activityCreateParamDto.setCredit(new BigDecimal(credit));
        }
        // 学时
        String period = FormUtils.getValue(formData, "period");
        if (StringUtils.isNotBlank(period)) {
            activityCreateParamDto.setPeriod(new BigDecimal(period));
        }
        // 最大参与时长
        String timeLengthUpperLimitStr = FormUtils.getValue(formData, "time_length_upper_limit");
        if (StringUtils.isNotBlank(timeLengthUpperLimitStr)) {
            Integer timeLengthUpperLimit = Integer.parseInt(timeLengthUpperLimitStr);
            activityCreateParamDto.setTimeLengthUpperLimit(timeLengthUpperLimit);
        }
        activityCreateParamDto.buildLoginUser(formData.getUid(), formData.getUname(), fid, orgName);
        activityCreateParamDto.setOriginType(Activity.OriginTypeEnum.ACTIVITY_DECLARATION.getValue());
        activityCreateParamDto.setOrigin(String.valueOf(formUserId));
        return activityCreateParamDto;
    }

    /**通过活动审批创建报名签到
     * @Description 
     * @author wwb
     * @Date 2021-06-11 17:49:43
     * @param formData
     * @param uid
     * @return com.chaoxing.activity.dto.sign.create.SignCreateParamDTO
    */
    private SignCreateParamDTO buildSignFromActivityApproval(WfwFormDTO formData, Integer uid) {
        SignCreateParamDTO signCreateParam = SignCreateParamDTO.buildDefault();
        // 报名
        List<SignUpCreateParamDTO> signUps = signCreateParam.getSignUps();
        SignUpCreateParamDTO signUp = signUps.get(0);
        String isOpenSignUp = FormUtils.getValue(formData, "is_open_sign_up");
        if (Objects.equals(YES, isOpenSignUp)) {
            signUp.setDeleted(false);
            String signUpOpenAudit = FormUtils.getValue(formData, "sign_up_open_audit");
            signUp.setOpenAudit(Objects.equals(YES, signUpOpenAudit));
            TimeScopeDTO signUpTimeScope = FormUtils.getTimeScope(formData, "sign_up_time");
            signUp.setStartTime(DateUtils.date2Timestamp(signUpTimeScope.getStartTime()));
            signUp.setEndTime(DateUtils.date2Timestamp(signUpTimeScope.getEndTime()));
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

        List<SignInCreateParamDTO> signIns = signCreateParam.getSignIns();
        // 签到
        SignInCreateParamDTO signIn = signIns.get(0);
        String isOpenSignIn = FormUtils.getValue(formData, "is_open_sign_in");
        if (Objects.equals(YES, isOpenSignIn)) {
            signIn.setDeleted(false);
            String signInPublicList = FormUtils.getValue(formData, "sign_in_public_list");
            signIn.setPublicList(Objects.equals(YES, signInPublicList));
            TimeScopeDTO signInTimeScope = FormUtils.getTimeScope(formData, "sign_in_time");
            signIn.setStartTime(DateUtils.date2Timestamp(signInTimeScope.getStartTime()));
            signIn.setEndTime(DateUtils.date2Timestamp(signInTimeScope.getEndTime()));
            String signInWay = FormUtils.getValue(formData, "sign_in_way");
            SignInCreateParamDTO.Way way = SignInCreateParamDTO.Way.fromName(signInWay);
            if (way != null) {
                signIn.setWay(way.getValue());
                if (Objects.equals(SignInCreateParamDTO.Way.POSITION, way)) {
                    AddressDTO signInAddress = FormUtils.getAddress(formData, "sign_in_address");
                    if (signInAddress != null) {
                        signIn.setAddress(signInAddress.getAddress());
                        signIn.setLongitude(signInAddress.getLng());
                        signIn.setDimension(signInAddress.getLat());
                    }
                }
            } else {
                signIn.setWay(SignInCreateParamDTO.Way.QR_CODE.getValue());
                SignInCreateParamDTO.ScanCodeWay scanCodeWay = SignInCreateParamDTO.ScanCodeWay.fromName(signInWay);
                if (scanCodeWay != null) {
                    signIn.setScanCodeWay(scanCodeWay.getValue());
                } else {
                    signIn.setScanCodeWay(SignInCreateParamDTO.ScanCodeWay.PARTICIPATOR.getValue());
                }
            }
        } else {
            signIn.setDeleted(true);
        }
        // 签退
        SignInCreateParamDTO signOut = signIns.get(1);
        String isOpenSignOut = FormUtils.getValue(formData, "is_open_sign_out");
        if (Objects.equals(YES, isOpenSignOut)) {
            signOut.setDeleted(false);
            String signOutPublicList = FormUtils.getValue(formData, "sign_out_public_list");
            signOut.setPublicList(Objects.equals(YES, signOutPublicList));
            TimeScopeDTO signOutTimeScope = FormUtils.getTimeScope(formData, "sign_out_time");
            signOut.setStartTime(DateUtils.date2Timestamp(signOutTimeScope.getStartTime()));
            signOut.setEndTime(DateUtils.date2Timestamp(signOutTimeScope.getEndTime()));
            String signOutWay = FormUtils.getValue(formData, "sign_out_way");
            SignInCreateParamDTO.Way way = SignInCreateParamDTO.Way.fromName(signOutWay);
            if (way != null) {
                signOut.setWay(way.getValue());
                if (Objects.equals(SignInCreateParamDTO.Way.POSITION, way)) {
                    AddressDTO signOutAddress = FormUtils.getAddress(formData, "sign_out_address");
                    if (signOutAddress != null) {
                        signOut.setAddress(signOutAddress.getAddress());
                        signOut.setLongitude(signOutAddress.getLng());
                        signOut.setDimension(signOutAddress.getLat());
                    }
                }
            } else {
                signOut.setWay(SignInCreateParamDTO.Way.QR_CODE.getValue());
                SignInCreateParamDTO.ScanCodeWay scanCodeWay = SignInCreateParamDTO.ScanCodeWay.fromName(signOutWay);
                if (scanCodeWay != null) {
                    signOut.setScanCodeWay(scanCodeWay.getValue());
                } else {
                    signOut.setScanCodeWay(SignInCreateParamDTO.ScanCodeWay.PARTICIPATOR.getValue());
                }
            }
        } else {
            signOut.setDeleted(true);
        }
        return signCreateParam;
    }

}