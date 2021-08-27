package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.ActivityFormSyncParamDTO;
import com.chaoxing.activity.dto.activity.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.activity.classify.MarketClassifyCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignInCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormDataDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormFieldDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateHandleService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/25 14:29
 * <p>
 */
@Slf4j
@Service
public class ActivityFormSyncService {

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
    private ClassifyQueryService classifyQueryService;
    @Resource
    private ClassifyHandleService classifyHandleService;
    @Resource
    private SignApiService signApiService;

    @Transactional(rollbackFor = Exception.class)
    public void syncCreateActivity(ActivityFormSyncParamDTO activityFormSyncParam) {
        Integer fid = activityFormSyncParam.getDeptId();
        Integer formId = activityFormSyncParam.getFormId();
        Integer formUserId = activityFormSyncParam.getIndexID();
        // 判断活动是否存在，若存在，则返回，不进行活动创建
        if (activityQueryService.existActivityCreateByForm(formId, formUserId)) {
            return;
        }
        // 获取表单数据
        WfwFormDTO formUserRecord = wfwFormApiService.getFormData(fid, formId, formUserId);
        if (formUserRecord == null) {
            throw new BusinessException("未查询到记录为:" + formUserId + "的表单数据");
        }
        // 获取活动创建者信息
        List<WfwAreaDTO> defaultPublishAreas = wfwAreaApiService.listByFid(fid);
        WfwAreaDTO orgInfo = Optional.ofNullable(defaultPublishAreas).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).findFirst().orElse(new WfwAreaDTO());
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(formUserRecord.getUid(), formUserRecord.getUname(), fid, orgInfo.getName());
        // 获取模板和市场信息
        Template template = marketHandleService.handleTemplateMarketByFidActivityFlag(fid, Activity.ActivityFlagEnum.THREE_CONFERENCE_ONE_LESSON, loginUser);
        // 封装活动创建信息数据
        ActivityCreateParamDTO activityCreateParam = packageActivityCreateParam(formUserRecord, template);
        activityCreateParam.setWebTemplateId(activityFormSyncParam.getWebTemplateId());
//        activityCreateParam.setWebTemplateId(22);
        activityCreateParam.setStatus(Activity.StatusEnum.RELEASED.getValue());
        activityCreateParam.setOrigin(String.valueOf(formId));
        activityCreateParam.setOriginFormUserId(formUserId);

        // 封装报名信息
        SignCreateParamDTO signCreateParam = SignCreateParamDTO.builder().name(activityCreateParam.getName()).build();
        // 默认开启报名
        signCreateParam.setSignUps(Lists.newArrayList(SignUpCreateParamDTO.buildDefault()));
        // 判断是否开启签到，并默认封装签到
        handleActivitySignIn(formUserRecord, signCreateParam);
        // 新增活动
        Integer activityId = activityHandleService.add(activityCreateParam, signCreateParam, defaultPublishAreas, loginUser);
        Activity activity = activityQueryService.getById(activityId);
        // 获取参与者列表, 进行用户报名
        List<Integer> participateUids = listParticipateUidByRecord(formUserRecord);
        signApiService.createUserSignUp(activity.getSignId(), participateUids);

        // 回写数据
        String data = packagePushUpdateData(fid, formId, activity);
        wfwFormApiService.updateFormData(formId, formUserId, data);
    }

    /**表单记录更新，同步更新活动
    * @Description
    * @author huxiaolong
    * @Date 2021-08-26 16:53:48
    * @param activityFormSyncParam
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void syncUpdateActivity(ActivityFormSyncParamDTO activityFormSyncParam) {
        Integer fid = activityFormSyncParam.getDeptId();
        Integer formId = activityFormSyncParam.getFormId();
        Integer formUserId = activityFormSyncParam.getIndexID();
        // 获取表单数据
        WfwFormDTO formUserRecord = wfwFormApiService.getFormData(fid, formId, formUserId);
        if (formUserRecord == null) {
            return;
        }
        Integer activityId = formUserRecord.getFormData().stream().filter(v -> Objects.equals(v.getAlias(), "activity_id")).map(u -> Optional.of(u.getValues().get(0)).map(v -> v.getInteger("val")).orElse(null)).findFirst().orElse(null);
        if (activityId == null) {
            // 记录信息中不存在活动id，不做操作
            return;
        }
        // 待更新数据
        ActivityUpdateParamDTO activityUpdateParam = new ActivityUpdateParamDTO();
        Activity activity = activityQueryService.getById(activityId);
        activityUpdateParam.buildFromActivity(activity);
        buildActivityUpdateParamFromFormRecord(formUserRecord, activityUpdateParam);
        // 报名签到
        Integer signId = activity.getSignId();
        SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
        if (signId != null) {
            sign = signApiService.getById(signId);
        }
        // 获取参会者，更新参会者信息
        List<Integer> participateUids = listParticipateUidByRecord(formUserRecord);
        signApiService.createUserSignUp(activity.getSignId(), participateUids);
        // 获取活动创建者信息
        List<WfwAreaDTO> defaultPublishAreas = wfwAreaApiService.listByFid(fid);
        WfwAreaDTO orgInfo = Optional.ofNullable(defaultPublishAreas).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).findFirst().orElse(new WfwAreaDTO());
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(formUserRecord.getUid(), formUserRecord.getUname(), fid, orgInfo.getName());
        // 根据表单记录数据，更新活动数据
        activityHandleService.edit(activityUpdateParam, sign, defaultPublishAreas, loginUser);
    }

    /**表单记录删除，同步删除活动
    * @Description
    * @author huxiaolong
    * @Date 2021-08-26 16:54:02
    * @param activityFormSyncParam
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void syncDeleteActivity(ActivityFormSyncParamDTO activityFormSyncParam) {
        Integer fid = activityFormSyncParam.getDeptId();
        Integer formId = activityFormSyncParam.getFormId();
        Integer formUserId = activityFormSyncParam.getIndexID();
        // 获取表单数据
        WfwFormDTO formUserRecord = wfwFormApiService.getFormData(fid, formId, formUserId);
        // 获取活动创建者信息
        WfwAreaDTO orgInfo = Optional.ofNullable(wfwAreaApiService.listByFid(fid)).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).findFirst().orElse(new WfwAreaDTO());
        LoginUserDTO loginUser = LoginUserDTO.buildDefault(formUserRecord.getUid(), formUserRecord.getUname(), fid, orgInfo.getName());
        // 获取待删除活动id
        Integer activityId = formUserRecord.getFormData().stream().filter(v -> Objects.equals(v.getAlias(), "activity_id")).map(u -> Optional.of(u.getValues().get(0)).map(v -> v.getInteger("val")).orElse(null)).findFirst().orElse(null);
        if (activityId == null) {
            // 记录信息中不存在活动id，返回
            return;
        }
        activityHandleService.delete(activityId, null, loginUser);
    }

    /**从表单数据中获取参与人uids
     * @Description
     * @author huxiaolong
     * @Date 2021-08-25 18:11:33
     * @param formUserRecord
     * @return java.util.List<java.lang.Integer>
     */
    private List<Integer> listParticipateUidByRecord(WfwFormDTO formUserRecord) {
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

    /**封装活动创建数据信息
     * @Description
     * @author huxiaolong
     * @Date 2021-08-25 15:52:10
     * @param formUserRecord
     * @return com.chaoxing.activity.dto.activity.ActivityCreateParamDTO
     */
    private ActivityCreateParamDTO packageActivityCreateParam(WfwFormDTO formUserRecord, Template template) {
        Integer marketId = template.getMarketId();
        ActivityCreateParamDTO activityCreateParam = ActivityCreateParamDTO.buildDefault();
        List<WfwFormDataDTO> formData = formUserRecord.getFormData();
        List<String> timeScopes = Lists.newArrayList();
        formData.forEach(v -> {
            List<JSONObject> values = v.getValues();
            if (!values.isEmpty()) {
                JSONObject obj = values.get(0);
                String attrValue = obj.getString("val");
                if (Objects.equals("activity_name", v.getAlias())) {
                    activityCreateParam.setName(attrValue);
                } else if (Objects.equals("activity_classify", v.getAlias())) {
                    Classify classify = classifyQueryService.getOrAddByName(attrValue);
                    MarketClassify marketClassify = classifyQueryService.getByClassifyIdAndMarketId(classify.getId(), marketId);
                    if (marketClassify == null) {
                        classifyHandleService.addMarketClassify(MarketClassifyCreateParamDTO.builder().marketId(marketId).name(attrValue).build());
                    }
                    activityCreateParam.setActivityClassifyId(classify.getId());
                } else if (Objects.equals("activity_address", v.getAlias())) {
                    activityCreateParam.setActivityType(Activity.ActivityTypeEnum.OFFLINE.getValue());
                    activityCreateParam.setAddress(attrValue);
                } else if (Objects.equals("activity_time_scope", v.getAlias())) {
                    timeScopes.add(attrValue);
                } else if (Objects.equals("introduction", v.getAlias())) {
                    activityCreateParam.setIntroduction(attrValue);
                }
            }
        });
        if (timeScopes.size() > 2) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime = StringUtils.isNotBlank(timeScopes.get(0)) ? LocalDateTime.parse(timeScopes.get(0), formatter) : null;
            LocalDateTime endTime = StringUtils.isNotBlank(timeScopes.get(1)) ? LocalDateTime.parse(timeScopes.get(1), formatter) : null;
            activityCreateParam.setStartTimeStamp(DateUtils.date2Timestamp(startTime));
            activityCreateParam.setEndTimeStamp(DateUtils.date2Timestamp(endTime));
        }
        activityCreateParam.setTemplateId(template.getId());
        activityCreateParam.setMarketId(marketId);
        return activityCreateParam;
    }

    /**封装回写数据
     * @Description
     * @author huxiaolong
     * @Date 2021-08-26 18:16:25
     * @param fid
     * @param formId
     * @param activity
     * @return java.lang.String
     */
    private String packagePushUpdateData(Integer fid, Integer formId, Activity activity) {
        List<WfwFormFieldDTO> formFieldInfos = wfwFormApiService.listFormField(fid, formId);
        JSONArray result = new JSONArray();
        for (WfwFormFieldDTO formInfo : formFieldInfos) {
            String alias = formInfo.getAlias();
            JSONObject item = new JSONObject();
            item.put("id", formInfo.getId());
            item.put("compt", formInfo.getCompt());
            item.put("comptId", formInfo.getId());
            item.put("alias", alias);
            JSONArray data = new JSONArray();
            if (Objects.equals(alias, "activity_id")) {
                data.add(activity.getId());
                item.put("val", data);
                result.add(item);
            } else if (Objects.equals(alias, "status")) {
                data.add(Activity.StatusEnum.fromValue(activity.getStatus()).getName());
                item.put("val", data);
                result.add(item);
            }
        }
        return result.toJSONString();
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-08-26 18:56:08
    * @param formUserRecord
    * @param signCreateParam
    * @return void
    */
    private void handleActivitySignIn(WfwFormDTO formUserRecord, SignCreateParamDTO signCreateParam) {
        WfwFormDataDTO formatData = formUserRecord.getFormData().stream().filter(v -> Objects.equals(v.getAlias(), "open_sign_in")).findFirst().orElse(null);
        if (formatData != null && CollectionUtils.isNotEmpty(formatData.getValues())) {
            String openSignIn = formatData.getValues().get(0).getString("val");
            if (!Objects.equals(openSignIn, "是")) {
                return;
            }
            String way = formatData.getValues().get(1).getString("val");
            List<SignInCreateParamDTO> signIns = Lists.newArrayList();
            SignInCreateParamDTO signIn = SignInCreateParamDTO.buildDefaultSignIn();
            if (Objects.equals(way, "普通签到")) {
                signIns.add(signIn);
            } else if (Objects.equals(way, "扫码签到")){
                signIn.setWay(3);
                signIn.setName("扫码签到");
                signIns.add(signIn);
            }
            signCreateParam.setSignIns(signIns);
        }

    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-08-26 17:55:11
    * @param formUserRecord
    * @param activityUpdateParam
    * @return void
    */
    private void buildActivityUpdateParamFromFormRecord(WfwFormDTO formUserRecord, ActivityUpdateParamDTO activityUpdateParam) {
        List<String> timeScopes = Lists.newArrayList();
        formUserRecord.getFormData().forEach(v -> {
            List<JSONObject> values = v.getValues();
            if (!values.isEmpty()) {
                JSONObject obj = values.get(0);
                String attrValue = obj.getString("val");
                if (Objects.equals("activity_name", v.getAlias())) {
                    activityUpdateParam.setName(attrValue);
                } else if (Objects.equals("activity_classify", v.getAlias())) {
                    Classify classify = classifyQueryService.getOrAddByName(attrValue);
                    MarketClassify marketClassify = classifyQueryService.getByClassifyIdAndMarketId(classify.getId(), activityUpdateParam.getMarketId());
                    if (marketClassify == null) {
                        classifyHandleService.addMarketClassify(MarketClassifyCreateParamDTO.builder().marketId(activityUpdateParam.getMarketId()).name(attrValue).build());
                    }
                    activityUpdateParam.setActivityClassifyId(classify.getId());
                } else if (Objects.equals("activity_address", v.getAlias())) {
                    activityUpdateParam.setActivityType(Activity.ActivityTypeEnum.OFFLINE.getValue());
                    activityUpdateParam.setAddress(attrValue);
                } else if (Objects.equals("activity_time_scope", v.getAlias())) {
                    timeScopes.add(attrValue);
                } else if (Objects.equals("introduction", v.getAlias())) {
                    activityUpdateParam.setIntroduction(attrValue);
                }
            }
        });
        if (timeScopes.size() > 2) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime = StringUtils.isNotBlank(timeScopes.get(0)) ? LocalDateTime.parse(timeScopes.get(0), formatter) : null;
            LocalDateTime endTime = StringUtils.isNotBlank(timeScopes.get(1)) ? LocalDateTime.parse(timeScopes.get(1), formatter) : null;
            activityUpdateParam.setStartTimeStamp(DateUtils.date2Timestamp(startTime));
            activityUpdateParam.setEndTimeStamp(DateUtils.date2Timestamp(endTime));
        }
    }

}
