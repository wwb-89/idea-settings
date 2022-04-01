package com.chaoxing.activity.service.queue.activity.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.DepartmentDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.create.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormDataItemDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignInCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.template.signup.SignUpWfwFormTemplateQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityPushReminderService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.CertificateApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.queue.activity.WfwFormActivityDataUpdateQueue;
import com.chaoxing.activity.util.ApplicationContextHolder;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.WfwFormUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.WfwFormAliasConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.util.exception.WfwFormActivityNotGeneratedException;
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

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/25 14:29
 * <p>
 */
@Slf4j
@Service
public class WfwFormSyncActivityQueueService {

    /** 是 */
    private static final String YES = "是";
    private static final String NO = "否";

    @Resource
    private ActivityQueryService activityQueryService ;
    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private WfwAreaApiService wfwAreaApiService;
    @Resource
    private WfwFormApiService wfwFormApiService;
    @Resource
    private MarketHandleService marketHandleService;
    @Resource
    private WebTemplateService webTemplateService;
    @Resource
    private ClassifyHandleService classifyHandleService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private TemplateQueryService templateQueryService;
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private WorkApiService workApiService;
    @Resource
    private SignUpWfwFormTemplateQueryService signUpWfwFormTemplateQueryService;

    @Resource
    private WfwFormActivityDataUpdateQueue wfwFormActivityDataUpdateQueue;
    @Resource
    private ActivityPushReminderService activityPushReminderService;
    @Resource
    private CertificateApiService certificateApiService;

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-09-01 14:05:07
    * @param fid
    * @param formId
    * @param formUserId
    * @return com.chaoxing.activity.model.Activity
    */
    public Activity getActivityFromFormInfo(Integer fid, Integer formId, Integer formUserId) {
        // 获取表单数据
        FormDataDTO formUserRecord = wfwFormApiService.getFormRecord(formUserId, formId, fid);
        if (formUserRecord == null) {
            throw new BusinessException("未查询到记录为:" + formUserId + "的表单数据");
        }
        Activity activity;
        Integer activityId = formUserRecord.getFormData().stream().filter(v -> Objects.equals(v.getAlias(), WfwFormAliasConstant.ACTIVITY_ID))
                .map(u -> {
                    if (CollectionUtils.isNotEmpty(u.getValues())) {
                        return u.getValues().get(0).getString("val");
                    }
                    return null;
                }).filter(StringUtils::isNotBlank).findFirst().map(Integer::parseInt).orElse(null);
        if (activityId == null) {
            activity = activityQueryService.getByFormUserId(formId, formUserId);
        } else {
            activity = activityQueryService.getById(activityId);
        }
        if (activity == null) {
            throw new WfwFormActivityNotGeneratedException();
        }
        return activity;
    }

    /**根据万能表单的数据创建活动
     * @Description 
     * @author huxiaolong
     * @Date 2021-11-26 14:55:41
     * @param fid
     * @param formId
     * @param formUserId
     * @param webTemplateId
     * @param flag
     * @return void
    */
    public void add(Integer fid, Integer formId, Integer formUserId, Integer webTemplateId, String flag) {
        flag = StringUtils.isNotBlank(flag) ? flag : Activity.ActivityFlagEnum.NORMAL.getValue();
        WfwFormSyncActivityQueueService wfwFormSyncActivityQueueService = ApplicationContextHolder.getBean(WfwFormSyncActivityQueueService.class);
        Activity activity = wfwFormSyncActivityQueueService.createActivity(fid, formId, formUserId, webTemplateId, flag);
        // 回写数据
        if (activity != null) {
            WfwFormActivityDataUpdateQueue.QueueParamDTO queueParam = WfwFormActivityDataUpdateQueue.QueueParamDTO.builder()
                    .activityId(activity.getId())
                    .fid(fid)
                    .formId(formId)
                    .formUserId(formUserId)
                    .build();
            wfwFormActivityDataUpdateQueue.push(queueParam);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Activity createActivity(Integer fid, Integer formId, Integer formUserId, Integer webTemplateId, String flag) {
        // 获取表单数据
        FormDataDTO formUserRecord = wfwFormApiService.getFormRecord(formUserId, formId, fid);
        if (formUserRecord == null) {
            log.error("未查询到fid:{}, formId:{}, formUserId:{} 的表单记录", fid, formId, formUserId);
            return null;
        }
        log.info("根据万能表单数据:{} 创建活动", JSON.toJSONString(formUserRecord));
        // 判断标识 activity_abort（boolean类型）为true则不创建
        Boolean activityAbort = formUserRecord.getBooleanValue("activity_abort");
        if (activityAbort) {
            return null;
        }
        // 判断活动是否存在，若存在，回写表单数据，并则返回；若不存在，则不进行活动创建
        Activity activity = activityQueryService.getByFormUserId(formId, formUserId);
        if (activity != null) {
            return activity;
        }
        // 获取机构名称
        String orgName = passportApiService.getOrgName(fid);
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(formUserRecord.getUid(), formUserRecord.getUname(), fid, orgName);
        // 获取模板和市场信息
        Integer marketId = marketHandleService.getOrCreateWfwFormMarket(fid, flag, formId, loginUser.buildOperateUserDTO());
        Template template = templateQueryService.getMarketFirstTemplate(marketId);
        // 活动分类
        String activityClassifyName = WfwFormUtils.getValue(formUserRecord, WfwFormAliasConstant.ACTIVITY_CLASSIFY);
        Classify classify = classifyHandleService.getOrAddMarketClassify(marketId, activityClassifyName);
        // 封装活动创建信息数据
        ActivityCreateParamDTO activityCreateParam = ActivityCreateParamDTO.buildFromFormData(formUserRecord, classify.getId(), orgName);
        activityCreateParam.setStatus(Activity.StatusEnum.RELEASED.getValue());
        activityCreateParam.setSignedUpNotice(true);
        if (activityCreateParam.getOpenWork()) {
            activityCreateParam.setWorkId(workApiService.createDefault(loginUser.getUid(), fid));
        }
        // 处理通知的叶子结构
        if (activityCreateParam.getOpenPushReminder()) {
            ActivityPushReminder activityPushReminder = activityPushReminderService.handleReminderScopesFromWfwForm(fid, activityCreateParam.getActivityPushReminder());
            activityCreateParam.setActivityPushReminder(activityPushReminder);
        }

        // 处理网页模版
        String webTemplateName = activityCreateParam.getWebTemplateName();
        if (webTemplateId == null && StringUtils.isNotBlank(webTemplateName)) {
            WebTemplate lastWebTemplate = webTemplateService.getLastByName(webTemplateName);
            webTemplateId = Optional.ofNullable(lastWebTemplate).map(WebTemplate::getId).orElse(null);
        }
        // 如果没有指定网页模版则使用默认的
        webTemplateId = Optional.ofNullable(webTemplateId).orElse(CommonConstant.DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID);
        // 补充额外必要信息
        Integer templateId = template.getId();
        activityCreateParam.setAdditionalAttrs(webTemplateId, marketId, templateId, flag);
        // 封装报名信息
        SignCreateParamDTO signCreateParam = SignCreateParamDTO.builder().name(activityCreateParam.getName()).build();
        // 获取报名信息
        SignUpCreateParamDTO signUpCreateParam = packageSignUp(formUserRecord, templateId, fid);
        // 报名配置万能表单填报信息
        OperateUserDTO operateUser = loginUser.buildOperateUserDTO();
        if (signUpCreateParam != null) {
            // 是否指定了报名使用的万能表单模板
            String signUpFormTemplateName = WfwFormUtils.getValue(formUserRecord, "sign_up_form_template_name");
            SignUpWfwFormTemplate signUpWfwFormTemplate = signUpWfwFormTemplateQueryService.getByNameOrDefaultSignUp(signUpFormTemplateName, signUpCreateParam);
            WfwFormCreateResultDTO wfwFormCreateResult =
                    wfwFormApiService.createWfwForm(operateUser.getFid(), operateUser.getUid(), signUpWfwFormTemplate);;
            if (wfwFormCreateResult != null) {
                // 根据表单模板和表单信息，填充报名表单信息相关属性
                signUpCreateParam.buildSignUpWfwFormInfo(signUpWfwFormTemplate, wfwFormCreateResult);
            }
            signCreateParam.setSignUps(Lists.newArrayList(signUpCreateParam));
        }
        // 判断是否开启签到，并默认封装签到
        SignInCreateParamDTO signInCreateParam = handleActivitySignIn(formUserRecord);
        if (signInCreateParam != null) {
            signCreateParam.setSignIns(Lists.newArrayList(signInCreateParam));
        }
        // 默认添加证书模板
        Integer certificateTemplateId = certificateApiService.copyTemplate(loginUser.getUid(), loginUser.getFid());
        activityCreateParam.setCertificateTemplateId(certificateTemplateId);
        // 新增活动
        List<WfwAreaDTO> defaultPublishAreas = wfwAreaApiService.listByFid(fid);
        Integer activityId = activityHandleService.add(activityCreateParam, signCreateParam, defaultPublishAreas, loginUser);
        String releaseStatus = WfwFormUtils.getValue(formUserRecord, WfwFormAliasConstant.ACTIVITY_RELEASE_STATUS);
        // 发布状态值不存在或不为未发布，发布活动
        if (!Objects.equals(releaseStatus, "未发布")) {
            // 立即发布
            activityHandleService.release(activityId, operateUser);
        }
        activity = activityQueryService.getById(activityId);
        // 获取参与者列表, 进行用户报名
        List<Integer> participateUids = listParticipateUidByRecord(formUserRecord);
        signApiService.createUserSignUp(activity.getSignId(), participateUids);
        return activity;
    }

    /**封装报名信息
    * @Description
    * @author huxiaolong
    * @Date 2021-10-09 14:57:03
    * @param formUserRecord
    * @param templateId
    * @return com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO
    */
    private SignUpCreateParamDTO packageSignUp(FormDataDTO formUserRecord, Integer templateId, Integer fid) {
        String openSignUp = WfwFormUtils.getValue(formUserRecord, "open_sign_up");
        Integer originId = templateComponentService.getSysComponentTplComponentId(templateId, "sign_up");
        SignUpCreateParamDTO signUpCreateParam = SignUpCreateParamDTO.buildDefault();
        signUpCreateParam.setOriginId(originId);
        if (Objects.equals(openSignUp, NO)) {
            return null;
        }
        if (Objects.equals(openSignUp, YES)) {
            // 报名时间
            TimeScopeDTO signUpTimeScope = resolveSignUpTime(formUserRecord);
            signUpCreateParam.setStartTime(DateUtils.date2Timestamp(signUpTimeScope.getStartTime()));
            signUpCreateParam.setEndTime(DateUtils.date2Timestamp(signUpTimeScope.getEndTime()));

            String signUpPersonLimit = WfwFormUtils.getValue(formUserRecord, "sign_up_person_limit");
            boolean personLimit = StringUtils.isNotBlank(signUpPersonLimit);
            signUpCreateParam.setLimitPerson(personLimit);
            if (personLimit) {
                signUpCreateParam.setPersonLimit(Integer.valueOf(signUpPersonLimit));
            }
            // 是否开启现场报名
            String signUpOnSite = WfwFormUtils.getValue(formUserRecord, "on_site_sign_up");
            signUpCreateParam.setOnSiteSignUp(Objects.equals(YES, signUpOnSite));
            String signUpEndAllowCancel = WfwFormUtils.getValue(formUserRecord, "sign_up_end_allow_cancel");
            signUpCreateParam.setEndAllowCancel(Objects.equals(YES, signUpEndAllowCancel));
            String signUpPublicList = WfwFormUtils.getValue(formUserRecord, "sign_up_public_list");
            signUpCreateParam.setPublicList(Objects.equals(YES, signUpPublicList));
            // 使用万能表单后 报名不支持审核
            signUpCreateParam.setOpenAudit(false);
            // 通讯录参与范围
            FormDataItemDTO contactPublishAreas = formUserRecord.getFormData().stream().filter(v -> Objects.equals(v.getAlias(), "contacts_participation_scope")).findFirst().orElse(null);
            if (contactPublishAreas != null) {
                List<Integer> departmentIds = WfwFormUtils.listDepartment(formUserRecord, "contacts_participation_scope")
                        .stream().map(DepartmentDTO::getId).collect(Collectors.toList());
                List<WfwGroupDTO> wfwGroups = wfwContactApiService.listUserContactOrgsByFid(fid)
                        .stream()
                        .filter(v -> departmentIds.contains(Integer.valueOf(v.getId()))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(wfwGroups)) {
                    signUpCreateParam.setEnableContactsParticipateScope(true);
                    List<SignUpParticipateScopeDTO> contactsParticipateScopes = Lists.newArrayList();
                    for (WfwGroupDTO wfwGroup : wfwGroups) {
                        contactsParticipateScopes.add(SignUpParticipateScopeDTO.buildFromWfwGroup(wfwGroup, "contacts"));
                    }
                    signUpCreateParam.setContactsParticipateScopes(contactsParticipateScopes);
                }
            }
        }
        return signUpCreateParam;
    }

    /**解析报名时间
     * @Description 
     * @author wwb
     * @Date 2021-12-30 10:17:53
     * @param formUserRecord
     * @return com.chaoxing.activity.dto.TimeScopeDTO
    */
    public TimeScopeDTO resolveSignUpTime(FormDataDTO formUserRecord) {
        return resolveSignUpTime(formUserRecord.getFormData());
    }

    public TimeScopeDTO resolveSignUpTime(List<FormDataItemDTO> formDataItems) {
        TimeScopeDTO signUpTimeScope = WfwFormUtils.getTimeScope(formDataItems, "sign_up_time_scope");
        LocalDateTime startTime = Optional.ofNullable(signUpTimeScope).map(TimeScopeDTO::getStartTime).orElse(null);
        LocalDateTime endTime = Optional.ofNullable(signUpTimeScope).map(TimeScopeDTO::getEndTime).orElse(null);
        if (startTime == null || endTime == null) {
            LocalDateTime now = LocalDateTime.now();
            if (startTime == null) {
                String signUpStartTimeStr = WfwFormUtils.getValue(formDataItems, "sign_up_start_time");
                startTime = StringUtils.isBlank(signUpStartTimeStr) ? now : WfwFormUtils.getTime(signUpStartTimeStr);
            }
            if (endTime == null) {
                String signUpEndTimeStr = WfwFormUtils.getValue(formDataItems, "sign_up_end_time");
                endTime = StringUtils.isBlank(signUpEndTimeStr) ? now.plusMonths(1) : WfwFormUtils.getTime(signUpEndTimeStr);
            }
        }
        return TimeScopeDTO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    /**表单记录更新，同步更新活动
    * @Description
    * @author huxiaolong
    * @Date 2021-08-26 16:53:48
    * @return void
    */
    public void update(Integer fid, Integer formId, Integer formUserId, Integer webTemplateId, String flag) {
        // 获取表单数据
        FormDataDTO formUserRecord = wfwFormApiService.getFormRecord(formUserId, formId, fid);
        if (formUserRecord == null) {
            log.error("表单数据推送根据参数fid:{}, formId:{}, formUserId:{} 获取表单记录为空", fid, formId, formUserId);
            return;
        }
        log.info("根据万能表单数据:{} 更新活动", JSON.toJSONString(formUserRecord));
        Activity activity = activityQueryService.getByFormUserId(formId, formUserId);
        if (activity == null) {
            // 查找表单记录中的活动id
            Integer activityId = formUserRecord.getIntegerValue(WfwFormAliasConstant.ACTIVITY_ID);
            if (activityId != null) {
                activity = activityQueryService.getById(activityId);
            }
        }
        // 若活动不存在，则新增
        if (activity == null) {
            flag = StringUtils.isNotBlank(flag) ? flag : Activity.ActivityFlagEnum.NORMAL.getValue();
            activity = ApplicationContextHolder.getBean(WfwFormSyncActivityQueueService.class).createActivity(fid, formId, formUserId, webTemplateId, flag);
        } else {
            activity = ApplicationContextHolder.getBean(WfwFormSyncActivityQueueService.class).update(formUserRecord, fid, activity.getId());
        }
        // 回写数据
        if (activity != null) {
            WfwFormActivityDataUpdateQueue.QueueParamDTO queueParam = WfwFormActivityDataUpdateQueue.QueueParamDTO.builder()
                    .activityId(activity.getId())
                    .fid(fid)
                    .formId(formId)
                    .formUserId(formUserId)
                    .build();
            wfwFormActivityDataUpdateQueue.push(queueParam);
        }
    }

    /**
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-27 17:53:14
    * @param formUserRecord
    * @param fid
    * @param activityId
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public Activity update(FormDataDTO formUserRecord, Integer fid, Integer activityId) {
        // 待更新数据
        ActivityUpdateParamDTO activityUpdateParam = new ActivityUpdateParamDTO();
        Activity activity = activityQueryService.getById(activityId);
        activityUpdateParam = activityUpdateParam.buildFromActivity(activity);
        // 活动分类
        String activityClassifyName = WfwFormUtils.getValue(formUserRecord, WfwFormAliasConstant.ACTIVITY_CLASSIFY);
        Classify classify = classifyHandleService.getOrAddMarketClassify(activity.getMarketId(), activityClassifyName);
        Integer classifyId = Optional.ofNullable(classify).map(Classify::getId).orElse(null);
        activityUpdateParam.fillFromFormData(formUserRecord, classifyId);
        // 报名签到
        Integer signId = activity.getSignId();
        SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
        if (signId != null) {
            sign = signApiService.getCreateById(signId);
        }
        // 获取参会者，更新参会者信息
        List<Integer> participateUids = listParticipateUidByRecord(formUserRecord);
        signApiService.createUserSignUp(activity.getSignId(), participateUids);
        // 获取活动创建者信息
        List<WfwAreaDTO> defaultPublishAreas = wfwAreaApiService.listByFid(fid);
        WfwAreaDTO orgInfo = Optional.ofNullable(defaultPublishAreas).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).findFirst().orElse(new WfwAreaDTO());
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(formUserRecord.getUid(), formUserRecord.getUname(), fid, orgInfo.getName());
        // 根据表单记录数据，更新活动数据
        activity = activityHandleService.edit(activityUpdateParam, sign, defaultPublishAreas, loginUser);
        String releaseStatus = WfwFormUtils.getValue(formUserRecord, WfwFormAliasConstant.ACTIVITY_RELEASE_STATUS);
        // 发布状态值不存在或不为未发布，发布活动
        if (Objects.equals(releaseStatus, "未发布")) {
            if (activity.getReleased()) {
                activityHandleService.cancelRelease(activityId, loginUser.buildOperateUserDTO());
            }
        } else {
            if (!activity.getReleased()) {
                activityHandleService.release(activityId, loginUser.buildOperateUserDTO());
            }
        }
        return activity;
    }

    /**从表单数据中获取参与人uids
     * @Description
     * @author huxiaolong
     * @Date 2021-08-25 18:11:33
     * @param formUserRecord
     * @return java.util.List<java.lang.Integer>
     */
    private List<Integer> listParticipateUidByRecord(FormDataDTO formUserRecord) {
        List<Integer> participateUids = Lists.newArrayList();
        formUserRecord.getFormData().forEach(v -> {
            if (Objects.equals("participate_users", v.getAlias())) {
                List<JSONObject> values = v.getValues();
                values.forEach(user -> {
                    participateUids.add(user.getInteger("puid"));
                });
            }
        });
        return participateUids;
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-08-26 18:56:08
    * @param formUserRecord
    * @return void
    */
    private SignInCreateParamDTO handleActivitySignIn(FormDataDTO formUserRecord) {
        FormDataItemDTO formatData = formUserRecord.getFormData().stream().filter(v -> Objects.equals(v.getAlias(), "open_sign_in")).findFirst().orElse(null);
        if (formatData == null || CollectionUtils.isEmpty(formatData.getValues())) {
            return null;
        }
        String openSignIn = formatData.getValues().get(0).getString("val");
        if (Objects.equals(openSignIn, NO) || Objects.equals(openSignIn, "无需签到")) {
            return null;
        }
        SignInCreateParamDTO signIn = SignInCreateParamDTO.buildDefaultSignIn();
        String way;
        if (Objects.equals(openSignIn, YES)) {
            way = formatData.getValues().get(1).getString("val");
        } else {
            way = openSignIn;
        }
        if (Objects.equals(way, "普通签到")) {
            signIn.setWay(1);
            signIn.setName("直接签到");
        } else if (Objects.equals(way, "扫码签到")) {
            signIn.setWay(3);
            signIn.setName("扫码签到");
        } else {
            // 不支持位置签到
            return null;
        }
        return signIn;
    }

    /**根据表单记录更新发布状态
     * @Description
     * @author huxiaolong
     * @Date 2021-11-13 00:36:58
     * @param fid
     * @param formId
     * @param uid
     * @param formUserId
     * @param marketId
     * @param flag
     * @param released
     * @return void
     */
    public void syncUpdateReleaseStatus(Integer fid, Integer formId, Integer uid, Integer formUserId, Integer marketId, String flag, Boolean released) {
        // 获取表单数据
        FormDataDTO formUserRecord = wfwFormApiService.getFormRecord(formUserId, formId, fid);
        if (formUserRecord == null) {
            log.error("未查询到记录为:" + formUserId + "的表单数据");
            return;
        }
        // 判断活动是否存在，若不存在，则不更新发布状态
        Integer activityId = Optional.ofNullable(WfwFormUtils.getValue(formUserRecord, WfwFormAliasConstant.ACTIVITY_ID)).filter(StringUtils::isNotBlank).map(Integer::valueOf).orElse(null);
        Activity activity;
        if (activityId == null) {
            activity = activityQueryService.getByFormUserId(formId, formUserId);
            activityId = Optional.ofNullable(activity).map(Activity::getId).orElse(null);
        } else {
            activity = activityQueryService.getById(activityId);
        }
        if (activity == null) {
            log.error("表单记录:" + formUserId + "不存在对应的活动");
            return;
        }
        if (marketId == null && StringUtils.isNotBlank(flag)) {
            marketId = marketQueryService.getMarketIdByFlag(fid, flag);
        }
        if (marketId == null) {
            log.error("市场id不存在");
            return;
        }
        OperateUserDTO operateUser = OperateUserDTO.build(activity.getCreateUid(), fid);
        if (released) {
            activityHandleService.releaseMarketActivity(activityId, marketId, operateUser);
        } else {
            activityHandleService.cancelReleaseMarketActivity(activityId, marketId, operateUser);
        }
        // 通知更新活动
        wfwFormActivityDataUpdateQueue.push(WfwFormActivityDataUpdateQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .fid(fid)
                .formId(formId)
                .formUserId(formUserId)
                .build());
    }

}
