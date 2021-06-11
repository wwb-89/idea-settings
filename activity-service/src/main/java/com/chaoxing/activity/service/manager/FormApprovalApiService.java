package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
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
        // 封面
        String cover = FormUtils.getValue(formData, "cover");
        // 开始时间、结束时间
        TimeScopeDTO activityTimeScope = FormUtils.getTimeScope(formData, "activity_time");
        activity.setStartTime(activityTimeScope.getStartTime());
        activity.setEndTime(activityTimeScope.getEndTime());

        String activityClassifyName = FormUtils.getValue(formData, "activity_classify");
        ActivityClassify activityClassify = activityClassifyHandleService.addAndGet(activityClassifyName, fid);
        String integralStr = FormUtils.getValue(formData, "integral_value");
        if (StringUtils.isNotBlank(integralStr)) {
            activity.setOpenIntegral(true);
            activity.setIntegralValue(BigDecimal.valueOf(Double.parseDouble(integralStr)));
        }

        activity.setActivityType(Activity.ActivityTypeEnum.ONLINE.getValue());
        activity.setName(activityName);
        activity.setActivityClassifyId(activityClassify.getId());
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
        return signAddEdit;
    }

}