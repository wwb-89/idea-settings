package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.*;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormAdvanceSearchFilterConditionDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignInCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.util.FormUtils;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
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
import javax.validation.constraints.NotNull;
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

    /** 获取表单指定数据url */
    private static final String LIST_FORM_SPECIFIED_DATA_URL = DomainConstant.WFW_FORM_API_DOMAIN + "/api/approve/forms/user/data/list";
    /** 获取表单数据列表 */
    private static final String ADVANCED_SEARCH_URL = DomainConstant.WFW_FORM_API_DOMAIN + "/api/approve/forms/advanced/search/list";
    /** 表单每页数据限制 */
    private static final int MAX_PAGE_SIZE_LIMIT = 100;

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
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private MarketHandleService marketHandleService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    private String getEnc(Map<String, Object> encParamMap) {
        StringBuilder enc = new StringBuilder();
        for (Map.Entry<String, Object> entry : encParamMap.entrySet()) {
            enc.append("[").append(entry.getKey()).append("=")
                    .append(entry.getValue()).append("]");
        }
        return DigestUtils.md5Hex(enc + "[" + KEY + "]");
    }

    /**获取表单记录
     * @Description
     * @author wwb
     * @Date 2021-08-30 11:35:11
     * @param formUserId
     * @param formId
     * @param fid
     * @return com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO
     */
    public FormDataDTO getFormRecord(@NotNull Integer formUserId, Integer formId, Integer fid) {
        List<Integer> formUserIds = Lists.newArrayList();
        formUserIds.add(formUserId);
        List<FormDataDTO> formDataDtos = listFormRecord(formUserIds, formId, fid);
        return Optional.ofNullable(formDataDtos).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**查询表单记录
     * @Description
     * @author wwb
     * @Date 2021-08-30 11:04:08
     * @param formUserIds
     * @param formId
     * @param fid
     * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
     */
    private List<FormDataDTO> listFormRecord(List<Integer> formUserIds, Integer formId, Integer fid) {
        if (CollectionUtils.isEmpty(formUserIds)) {
            return Lists.newArrayList();
        }
        TreeMap<String, Object> paramsMap = Maps.newTreeMap();
        paramsMap.put("deptId", fid);
        paramsMap.put("formId", formId);
        paramsMap.put("formUserIds", String.join(",", Optional.of(formUserIds).orElse(Lists.newArrayList()).stream().map(String::valueOf).collect(Collectors.toList())));
        paramsMap.put("sign", SIGN);
        paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        paramsMap.put("enc", getEnc(paramsMap));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(paramsMap);
        String result = restTemplate.postForObject(LIST_FORM_SPECIFIED_DATA_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getBoolean("success")) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("formUserList");
            if (jsonArray != null) {
                return JSON.parseArray(jsonArray.toJSONString(), FormDataDTO.class);
            } else {
                return Lists.newArrayList();
            }
        } else {
            String errorMessage = jsonObject.getString("msg");
            throw new BusinessException(errorMessage);
        }
    }

    /**查询表单下的所有数据
     * @Description
     * @author wwb
     * @Date 2021-08-30 16:08:14
     * @param formId
     * @param fid
     * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
     */
    public List<FormDataDTO> listFormRecord(Integer formId, Integer fid) {
        FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto = FormAdvanceSearchFilterConditionDTO.builder()
                .model(FormAdvanceSearchFilterConditionDTO.ModelEnum.AND.getValue())
                .filters(Lists.newArrayList())
                .build();
        return advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid, 1, MAX_PAGE_SIZE_LIMIT);
    }


    /**高级检索表单数据
     * @Description
     * @author wwb
     * @Date 2021-08-30 22:43:47
     * @param formAdvanceSearchFilterConditionDto
     * @param formId
     * @param fid
     * @param pageNum
     * @param pageSize
     * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
     */
    public List<FormDataDTO> advancedSearchAll(FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto, Integer formId, Integer fid, Integer pageNum, Integer pageSize) {
        TreeMap<String, Object> paramsMap = Maps.newTreeMap();
        paramsMap.put("deptId", fid);
        paramsMap.put("formId", formId);
        paramsMap.put("cpage", pageNum);
        paramsMap.put("pageSize", pageSize);
        paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        paramsMap.put("sign", SIGN);
        paramsMap.put("enc", getEnc(paramsMap));
        paramsMap.put("searchStr", JSON.toJSONString(formAdvanceSearchFilterConditionDto));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(paramsMap);
        String result = restTemplate.postForObject(ADVANCED_SEARCH_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getBoolean("success")) {
            List<FormDataDTO> formDataDtos = Lists.newArrayList();
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jsonArray = data.getJSONArray("dataList");
            formDataDtos.addAll(FormDataDTO.buildFromAdvanceSearchResult(jsonArray));
            Integer totalPage = data.getInteger("totalPage");
            if (totalPage > pageNum) {
                formDataDtos.addAll(advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid, ++pageNum, pageSize));
            }
            return formDataDtos;
        } else {
            String errorMessage = jsonObject.getString("msg");
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
        FormDataDTO formData = getFormRecord(formUserId, formId, fid);
        if (formData == null) {
            return;
        }
        if (!Objects.equals(formData.getAprvStatusTypeId(), CommonConstant.FORM_APPROVAL_AGREE_VALUE)) {
            // 审批不通过的忽略
            return;
        }
        // 是否已经创建了活动，根据formUserId来判断
        Activity existActivity = activityQueryService.getByOriginTypeAndOrigin(Activity.OriginTypeEnum.ACTIVITY_DECLARATION, String.valueOf(formUserId));
        if (existActivity != null) {
            return;
        }
        String orgName = passportApiService.getOrgName(fid);
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(formData.getUid(), formData.getUname(), fid, orgName);
        // 活动市场数据处理
        Integer templateId = null;
        if (marketId != null || StringUtils.isNotBlank(flag)) {
            if (marketId == null) {
                marketId = marketHandleService.getOrCreateOrgMarket(fid, Activity.ActivityFlagEnum.fromValue(flag), loginUser);
            }
            List<Market> markets = marketQueryService.listByFid(fid);
            if (CollectionUtils.isEmpty(markets)) {
                log.error("机构:{} 活动市场不存在", fid);
                return;
            }
            if (!markets.stream().map(Market::getId).collect(Collectors.toList()).contains(marketId)) {
                log.error("机构:{} 活动市场:{} 不存在", fid, marketId);
                return;
            }
            Template template = templateQueryService.getMarketFirstTemplate(marketId);
            if (template == null) {
                log.error("活动市场:{}下不存在模版", marketId);
                return;
            }
            templateId = template.getId();
            flag = template.getActivityFlag();
        }
        // 使用指定的模板
        webTemplateId = Optional.ofNullable(webTemplateId).orElse(CommonConstant.DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID);
        WebTemplate webTemplate = webTemplateService.getById(webTemplateId);
        if (webTemplate == null) {
            throw new BusinessException("通过活动申报创建活动指定的门户模版不存在");
        }
        // 获取活动分类
        String activityClassifyName = FormUtils.getValue(formData, "activity_classify");
        Classify classify;
        if (marketId == null) {
            classify = classifyHandleService.getOrAddOrgClassify(fid, activityClassifyName);
            // 设置模板id为系统模板
            Template template = templateQueryService.getSystemTemplateByActivityFlag(Activity.ActivityFlagEnum.NORMAL);
            templateId = Optional.ofNullable(template).map(Template::getId).orElse(null);
        } else {
            classify = classifyHandleService.getOrAddMarketClassify(marketId, activityClassifyName);
        }
        // 根据表单数据创建活动
        ActivityCreateParamDTO activity = ActivityCreateParamDTO.buildFromFormData(formData, classify.getId(), orgName);
        activity.setOriginType(Activity.OriginTypeEnum.ACTIVITY_DECLARATION.getValue());
        activity.setOrigin(String.valueOf(formUserId));
        // 补充活动必要信息
        activity.setAdditionalAttrs(webTemplateId, marketId, templateId, flag);
        // 根据表单数据创建报名签到
        SignCreateParamDTO signCreateParam = buildSignFromActivityApproval(formData, loginUser.getUid(), fid, DateUtils.timestamp2Date(activity.getStartTimeStamp()), DateUtils.timestamp2Date(activity.getEndTimeStamp()));
        if (CollectionUtils.isNotEmpty(signCreateParam.getSignUps())) {
            Integer originId = templateComponentService.getSysComponentTplComponentId(templateId, "sign_up");
            signCreateParam.getSignUps().get(0).setOriginId(originId);
        }
        WfwAreaDTO wfwRegionalArchitecture = wfwAreaApiService.buildWfwRegionalArchitecture(fid);
        Integer activityId = activityHandleService.add(activity, signCreateParam, Lists.newArrayList(wfwRegionalArchitecture), loginUser);
        OperateUserDTO operateUser = loginUser.buildOperateUserDTO();
        // 发布
        activityHandleService.release(activityId, operateUser);
    }

    /**通过活动审批创建报名签到
     * @Description
     * @author wwb
     * @Date 2021-06-11 17:49:43
     * @param formData
     * @param uid
     * @param activityStartTime
     * @param activityEndTime
     * @return com.chaoxing.activity.dto.sign.create.SignCreateParamDTO
     */
    private SignCreateParamDTO buildSignFromActivityApproval(FormDataDTO formData, Integer uid, Integer fid, LocalDateTime activityStartTime, LocalDateTime activityEndTime) {
        SignCreateParamDTO signCreateParam = SignCreateParamDTO.buildDefault();
        // 报名
        List<SignUpCreateParamDTO> signUps = signCreateParam.getSignUps();
        SignUpCreateParamDTO signUp = signUps.get(0);
        String isOpenSignUp = FormUtils.getValue(formData, "is_open_sign_up");
        if (Objects.equals(YES, isOpenSignUp)) {
            signUp.setDeleted(false);
            String signUpOpenAudit = FormUtils.getValue(formData, "sign_up_open_audit");
            signUp.setOpenAudit(Objects.equals(YES, signUpOpenAudit));
            String signUpOnSite = FormUtils.getValue(formData, "on_site_sign_up");
            signUp.setOnSiteSignUp(Objects.equals(YES, signUpOnSite));
            TimeScopeDTO signUpTimeScope = FormUtils.getTimeScope(formData, "sign_up_time");
            LocalDateTime startTime = Optional.ofNullable(signUpTimeScope.getStartTime()).orElse(LocalDateTime.now());
            LocalDateTime endTime = Optional.ofNullable(signUpTimeScope.getEndTime()).orElse(startTime.plusMonths(1));
            signUp.setStartTime(DateUtils.date2Timestamp(startTime));
            signUp.setEndTime(DateUtils.date2Timestamp(endTime));
            String signUpEndAllowCancel = FormUtils.getValue(formData, "sign_up_end_allow_cancel");
            signUp.setEndAllowCancel(Objects.equals(YES, signUpEndAllowCancel));
            String signUpPublicList = FormUtils.getValue(formData, "sign_up_public_list");
            signUp.setPublicList(Objects.equals(YES, signUpPublicList));
            String signUpPersonLimit = FormUtils.getValue(formData, "sign_up_person_limit");
            List<DepartmentDTO> departments = FormUtils.listDepartment(formData, "sign_up_contacts_scope");
            if (CollectionUtils.isNotEmpty(departments)) {
                List<Integer> departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
                List<WfwGroupDTO> wfwGroups = wfwContactApiService.listUserContactOrgsByFid(fid)
                        .stream()
                        .filter(v -> departmentIds.contains(Integer.valueOf(v.getId()))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(wfwGroups)) {
                    signUp.setEnableContactsParticipateScope(true);
                    List<SignUpParticipateScopeDTO> contactsParticipateScopes = Lists.newArrayList();
                    for (WfwGroupDTO wfwGroup : wfwGroups) {
                        contactsParticipateScopes.add(SignUpParticipateScopeDTO.buildFromWfwGroup(wfwGroup, "contacts"));
                    }
                    signUp.setContactsParticipateScopes(contactsParticipateScopes);
                }
            }
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
            LocalDateTime signInStartTime = Optional.ofNullable(signInTimeScope.getStartTime()).orElse(activityStartTime.minusHours(1));
            LocalDateTime signInEndTime = Optional.ofNullable(signInTimeScope.getEndTime()).orElse(null);
            signIn.setStartTime(DateUtils.date2Timestamp(signInStartTime));
            signIn.setEndTime(DateUtils.date2Timestamp(signInEndTime));
            String signInWay = FormUtils.getValue(formData, "sign_in_way");
            // 获取的签到形式选项有：普通签到、参与者扫码、管理者扫码
            SignInCreateParamDTO.Way way = SignInCreateParamDTO.Way.fromName(signInWay);
            if (way != null) {
                // 普通签到或者位置签到
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
                // 扫码签到
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
            LocalDateTime signOutStartTime = Optional.ofNullable(signOutTimeScope.getStartTime()).orElse(activityStartTime);
            LocalDateTime signOutEndTime = Optional.ofNullable(signOutTimeScope.getEndTime()).orElse(null);
            signOut.setStartTime(DateUtils.date2Timestamp(signOutStartTime));
            signOut.setEndTime(DateUtils.date2Timestamp(signOutEndTime));
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