package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.dto.*;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormDataItemDTO;
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
import com.chaoxing.activity.service.activity.manager.ActivityPushReminderService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwApprovalApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.WfwFormUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**微服务审批创建活动队列服务
 * @author wwb
 * @version ver 1.0
 * @className WfwApprovalActivityCreateQueueService
 * @description
 * @blame wwb
 * @date 2021-12-29 16:03:50
 */
@Slf4j
@Service
public class WfwApprovalActivityCreateQueueService {

    /** 是 */
    private static final String YES = "是";

    @Resource
    private WfwApprovalApiService wfwApprovalApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private MarketHandleService marketHandleService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private TemplateQueryService templateQueryService;
    @Resource
    private WebTemplateService webTemplateService;
    @Resource
    private ClassifyHandleService classifyHandleService;
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private WfwAreaApiService wfwAreaApiService;
    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private ActivityPushReminderService activityPushReminderService;

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
    public void handle(Integer fid, Integer formId, Integer formUserId, Integer marketId, String flag, Integer webTemplateId) {
        // 获取表单数据
        FormDataDTO formData = wfwApprovalApiService.getFormRecord(formUserId, formId, fid);
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
                marketId = marketHandleService.getOrCreateMarket(fid, Activity.ActivityFlagEnum.fromValue(flag), loginUser);
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
        String activityClassifyName = WfwFormUtils.getValue(formData, "activity_classify");
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
        // 处理通知的叶子结构
        if (activity.getOpenPushReminder()) {
            ActivityPushReminder activityPushReminder = activityPushReminderService.handleReminderScopesFromWfwForm(fid, activity.getActivityPushReminder());
            activity.setActivityPushReminder(activityPushReminder);
        }
        // 根据表单数据创建报名签到
        SignCreateParamDTO signCreateParam = buildSignFromApproval(formData, loginUser.getUid(), fid, DateUtils.timestamp2Date(activity.getStartTimeStamp()));
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
     * @param fid
     * @param activityStartTime
     * @return com.chaoxing.activity.dto.sign.create.SignCreateParamDTO
     */
    private SignCreateParamDTO buildSignFromApproval(FormDataDTO formData, Integer uid, Integer fid, LocalDateTime activityStartTime) {
        SignCreateParamDTO signCreateParam = SignCreateParamDTO.buildDefault();
        // 报名
        List<SignUpCreateParamDTO> signUps = signCreateParam.getSignUps();
        SignUpCreateParamDTO signUp = signUps.get(0);
        String isOpenSignUp = WfwFormUtils.getValue(formData, "is_open_sign_up");
        if (Objects.equals(YES, isOpenSignUp)) {
            signUp.setDeleted(false);
            String signUpOpenAudit = WfwFormUtils.getValue(formData, "sign_up_open_audit");
            signUp.setOpenAudit(Objects.equals(YES, signUpOpenAudit));
            String signUpOnSite = WfwFormUtils.getValue(formData, "on_site_sign_up");
            signUp.setOnSiteSignUp(Objects.equals(YES, signUpOnSite));
            // 报名时间
            TimeScopeDTO signUpTimeScope = resolveSignUpTime(formData);
            signUp.setStartTime(DateUtils.date2Timestamp(signUpTimeScope.getStartTime()));
            signUp.setEndTime(DateUtils.date2Timestamp(signUpTimeScope.getEndTime()));

            String signUpEndAllowCancel = WfwFormUtils.getValue(formData, "sign_up_end_allow_cancel");
            signUp.setEndAllowCancel(Objects.equals(YES, signUpEndAllowCancel));
            String signUpPublicList = WfwFormUtils.getValue(formData, "sign_up_public_list");
            signUp.setPublicList(Objects.equals(YES, signUpPublicList));
            String signUpPersonLimit = WfwFormUtils.getValue(formData, "sign_up_person_limit");
            List<DepartmentDTO> departments = WfwFormUtils.listDepartment(formData, "sign_up_contacts_scope");
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
            List<String> fieldNames = WfwFormUtils.listValue(formData, "sign_up_fill_info");
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
        String isOpenSignIn = WfwFormUtils.getValue(formData, "is_open_sign_in");
        if (Objects.equals(YES, isOpenSignIn)) {
            signIn.setDeleted(false);
            String signInPublicList = WfwFormUtils.getValue(formData, "sign_in_public_list");
            signIn.setPublicList(Objects.equals(YES, signInPublicList));
            // 签到时间
            TimeScopeDTO signInTimeScope = resolveSignInTime(formData, activityStartTime);
            signIn.setStartTime(DateUtils.date2Timestamp(signInTimeScope.getStartTime()));
            signIn.setEndTime(DateUtils.date2Timestamp(signInTimeScope.getEndTime()));
            String signInWay = WfwFormUtils.getValue(formData, "sign_in_way");
            // 获取的签到形式选项有：普通签到、参与者扫码、管理者扫码
            SignInCreateParamDTO.Way way = SignInCreateParamDTO.Way.fromName(signInWay);
            if (way != null) {
                // 普通签到或者位置签到
                signIn.setWay(way.getValue());
                if (Objects.equals(SignInCreateParamDTO.Way.POSITION, way)) {
                    AddressDTO signInAddress = WfwFormUtils.getAddress(formData, "sign_in_address");
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
        String isOpenSignOut = WfwFormUtils.getValue(formData, "is_open_sign_out");
        if (Objects.equals(YES, isOpenSignOut)) {
            signOut.setDeleted(false);
            String signOutPublicList = WfwFormUtils.getValue(formData, "sign_out_public_list");
            signOut.setPublicList(Objects.equals(YES, signOutPublicList));
            TimeScopeDTO signOutTimeScope = resolveSignOutTime(formData, activityStartTime);
            signOut.setStartTime(DateUtils.date2Timestamp(signOutTimeScope.getStartTime()));
            signOut.setEndTime(DateUtils.date2Timestamp(signOutTimeScope.getEndTime()));
            String signOutWay = WfwFormUtils.getValue(formData, "sign_out_way");
            SignInCreateParamDTO.Way way = SignInCreateParamDTO.Way.fromName(signOutWay);
            if (way != null) {
                signOut.setWay(way.getValue());
                if (Objects.equals(SignInCreateParamDTO.Way.POSITION, way)) {
                    AddressDTO signOutAddress = WfwFormUtils.getAddress(formData, "sign_out_address");
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

    public TimeScopeDTO resolveSignUpTime(FormDataDTO formData) {
        return resolveSignUpTime(formData.getFormData());
    }

    public TimeScopeDTO resolveSignUpTime(List<FormDataItemDTO> formDataItems) {
        TimeScopeDTO timeScope = WfwFormUtils.getTimeScope(formDataItems, "sign_up_time");
        LocalDateTime startTime = Optional.ofNullable(timeScope).map(TimeScopeDTO::getStartTime).orElse(LocalDateTime.now());
        LocalDateTime endTime = Optional.ofNullable(timeScope).map(TimeScopeDTO::getEndTime).orElse(startTime.plusMonths(1));
        return TimeScopeDTO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public TimeScopeDTO resolveSignInTime(FormDataDTO formData, LocalDateTime activityStartTime) {
        return resolveSignInTime(formData.getFormData(), activityStartTime);
    }

    public TimeScopeDTO resolveSignInTime(List<FormDataItemDTO> formDataItems, LocalDateTime activityStartTime) {
        activityStartTime = Optional.ofNullable(activityStartTime).orElse(LocalDateTime.now());
        TimeScopeDTO timeScope = WfwFormUtils.getTimeScope(formDataItems, "sign_in_time");
        LocalDateTime startTime = Optional.ofNullable(timeScope).map(TimeScopeDTO::getStartTime).orElse(activityStartTime.minusHours(1));
        LocalDateTime endTime = Optional.ofNullable(timeScope).map(TimeScopeDTO::getEndTime).orElse(null);
        return TimeScopeDTO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public TimeScopeDTO resolveSignOutTime(FormDataDTO formData, LocalDateTime activityStartTime) {
        return resolveSignOutTime(formData.getFormData(), activityStartTime);
    }

    public TimeScopeDTO resolveSignOutTime(List<FormDataItemDTO> formDataItems, LocalDateTime activityStartTime) {
        activityStartTime = Optional.ofNullable(activityStartTime).orElse(LocalDateTime.now());
        TimeScopeDTO timeScope = WfwFormUtils.getTimeScope(formDataItems, "sign_out_time");
        LocalDateTime startTime = Optional.ofNullable(timeScope).map(TimeScopeDTO::getStartTime).orElse(activityStartTime);
        LocalDateTime endTime = Optional.ofNullable(timeScope).map(TimeScopeDTO::getEndTime).orElse(null);
        return TimeScopeDTO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

}