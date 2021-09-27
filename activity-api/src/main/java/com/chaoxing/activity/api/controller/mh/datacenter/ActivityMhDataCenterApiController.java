package com.chaoxing.activity.api.controller.mh.datacenter;

import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.api.util.MhPreParamsUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.dto.manager.mh.MhMarketDataCenterDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/16 10:45
 * <p>
 */
@RestController
@RequestMapping("mh/data-center")
public class ActivityMhDataCenterApiController {

    @Resource
    private WfwAreaApiService wfwAreaApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCoverUrlSyncService activityCoverUrlSyncService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private ClassifyQueryService classifyQueryService;
    @Resource
    private ActivityComponentValueService activityComponentValueService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private PassportApiService passportApiService;


    /**获取机构下市场的门户数据源接口地址
    * @Description
    * @author huxiaolong
    * @Date 2021-09-16 11:40:16
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping()
    public RestRespDTO listMhMarketDataUrl(@RequestBody String data) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        // 查询所有市场id
        List<Market> markets = marketQueryService.listByFid(wfwfid);
        List<MhMarketDataCenterDTO> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(markets)) {
            markets.forEach(v -> result.add(MhMarketDataCenterDTO.buildFromMarket(v)));
        }
        result.add(MhMarketDataCenterDTO.builder()
                .name("我的活动")
                .divUrl(UrlConstant.API_DOMAIN + "/mh/data-center/my").build());
        return RestRespDTO.success(result);
    }

    /**查询活动市场marketId的活动数据
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:57:11
    * @param data
* @param marketId
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("market/{marketId}")
    public RestRespDTO index(@RequestBody String data, @PathVariable Integer marketId) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        String sw = params.getString("sw");

        Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
        Integer pageNum = params.getInteger("pageNum");
        pageNum = Optional.ofNullable(pageNum).orElse(1);
        Integer pageSize = params.getInteger("pageSize");
        pageSize = Optional.ofNullable(pageSize).orElse(12);
        Integer activityClassifyId = null;
        String classifies = params.getString("classifies");
        if (StringUtils.isNotBlank(classifies)) {
            JSONArray jsonArray = JSON.parseArray(classifies);
            if (jsonArray.size() > 0) {
                JSONObject activityClassify = jsonArray.getJSONObject(0);
                activityClassifyId = activityClassify.getInteger("id");
            }
        }
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        // 状态
        String statusParams = urlParams.getString("status");
        List<Integer> statusList = MhPreParamsUtils.resolveIntegerV(statusParams);
        // flag
        String flag = urlParams.getString("flag");
        ActivityQueryDTO activityQuery = ActivityQueryDTO.builder()
                .topFid(wfwfid)
                .statusList(statusList)
                .marketId(marketId)
                .flag(flag)
                .sw(sw)
                .activityClassifyId(activityClassifyId)
                .build();
        List<Integer> fids = wfwAreaApiService.listSubFid(wfwfid);
        activityQuery.setFids(fids);
        Page<Activity> page = new Page(pageNum, pageSize);
        page = activityQueryService.listParticipate(page, activityQuery);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<Activity> records = page.getRecords();
        JSONArray activityJsonArray = packageActivities(records);
        jsonObject.put("results", activityJsonArray);
        return RestRespDTO.success(jsonObject);
    }

    /**封装活动数据为门户标准接收格式
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:57:46
    * @param activities
    * @return com.alibaba.fastjson.JSONArray
    */
    private JSONArray packageActivities(List<Activity> activities) {
        JSONArray activityJsonArray = new JSONArray();
        if (CollectionUtils.isEmpty(activities)) {
            return activityJsonArray;
        }
        System.out.println();
        Map<Integer, Integer> activityTemplateMap = activities.stream().filter(v -> v.getTemplateId() != null).collect(Collectors.toMap(Activity::getId, Activity::getTemplateId, (v1, v2) -> v2));
        List<Integer> activityIds = activities.stream().map(Activity::getId).collect(Collectors.toList());
        List<Integer> signIds = activities.stream().map(Activity::getSignId).collect(Collectors.toList());
        Map<Integer, SignStatDTO> signStatMap = signApiService.statSignSignUps(signIds).stream().collect(Collectors.toMap(SignStatDTO::getId, v -> v, (v1, v2) -> v2));
        long time1 = System.currentTimeMillis();
        Map<Integer, List<ActivityComponentValueDTO>> activityComponentValuesMap = activityComponentValueService.listActivityComponentValues(activityTemplateMap);
        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);

        Map<Integer, String> introductionMap = activityQueryService.listDetailByActivityIds(activityIds).stream().collect(Collectors.toMap(ActivityDetail::getActivityId, v -> HtmlUtil.cleanHtmlTag(v.getIntroduction()), (v1, v2) -> v2));

        for (Activity record : activities) {
            Map<String, String> fieldCodeNameMap = activityQueryService.getFieldCodeNameRelation(record);
            SignStatDTO signStat = signStatMap.get(record.getSignId());
            // 活动
            JSONObject activity = new JSONObject();
            Integer activityId = record.getId();
            activity.put("id", activityId);
            activity.put("type", 3);
            activity.put("orsUrl", record.getPreviewUrl());
            JSONArray fields = new JSONArray();
            activity.put("fields", fields);
            int fieldFlag = 0;
            // 封面
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_cover", "封面"), activityCoverUrlSyncService.getCoverUrl(record), fieldFlag));
            // 活动名称
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_name", "名称"), record.getName(), ++fieldFlag));
            Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(record.getActivityType());
            String activityAddress = record.getAddress();
            if (StringUtils.isNotBlank(activityAddress)) {
                String detailAddress = record.getDetailAddress();
                if (StringUtils.isNotBlank(detailAddress)) {
                    activityAddress += detailAddress;
                }
            }
            // 类型
            String activityType = Optional.ofNullable(activityTypeEnum).map(Activity.ActivityTypeEnum::getName).orElse(StringUtils.isBlank(activityAddress) ? Activity.ActivityTypeEnum.ONLINE.getName() : Activity.ActivityTypeEnum.OFFLINE.getName());
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_type", "类型"), activityType, ++fieldFlag));

            // 地点
            fields.add(buildField("地点", activityAddress, ++fieldFlag));
            // 主办方
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_organisers", "主办方"), record.getOrganisers(), ++fieldFlag));
            // 开始结束时间
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_time_scope", "活动时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()), ++fieldFlag));
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_time_scope", "活动时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()), ++fieldFlag));

            // 报名开始结束时间
            fields.add(buildField(fieldCodeNameMap.getOrDefault("sign_up_time_scope", "报名时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpStartTime()), ++fieldFlag));
            fields.add(buildField(fieldCodeNameMap.getOrDefault("sign_up_time_scope", "报名时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpEndTime()), ++fieldFlag));

            // 活动状态
            Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(record.getStatus());
            fields.add(buildField("活动状态", statusEnum.getName(), ++fieldFlag));
            // 报名状态
            fields.add(buildField("报名状态", getSignUpStatus(signStat), ++fieldFlag));
            // 简介（40字纯文本）
            fields.add(buildField(fieldCodeNameMap.getOrDefault("introduction", "简介"), introductionMap.get(activityId), ++fieldFlag));
            // 已报名人数
            fields.add(buildField("已报名人数", signStat.getSignedUpNum(), ++fieldFlag));
            // 人数限制
            fields.add(buildField(fieldCodeNameMap.getOrDefault("sign_up_person_limit", "人数限制"), signStat.getLimitNum().compareTo(0) == 0 ? "不限" : signStat.getLimitNum(), ++fieldFlag));
            // 活动分类
            fields.add(buildField(fieldCodeNameMap.getOrDefault("activity_classify", "分类"), record.getActivityClassifyName(), ++fieldFlag));
            // 标签
            fields.add(buildField(fieldCodeNameMap.getOrDefault("tags", "标签"), record.getTags(), ++fieldFlag));
            // 自定义字段标题和值
            List<ActivityComponentValueDTO> componentValues = activityComponentValuesMap.get(activityId);
            if (CollectionUtils.isNotEmpty(componentValues)) {
                for (ActivityComponentValueDTO componentValue : componentValues) {
                    fields.add(buildField(componentValue.getTemplateComponentName(), componentValue.getValue(), ++fieldFlag));

                }
            }
            // 模板的分类
            fields.add(buildField("活动标识", record.getActivityFlag(), ++fieldFlag));
            activityJsonArray.add(activity);
        }
        return activityJsonArray;
    }

    /**查询市场下分类的列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:58:09
    * @param data
    * @param marketId
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("market/{marketId}/classifies")
    public RestRespDTO listClassify(@RequestBody String data, @PathVariable Integer marketId) {
        List<Classify> classifies;
        if (marketId != null) {
            classifies = classifyQueryService.listMarketClassifies(marketId);
        } else {
            JSONObject params = JSON.parseObject(data);
            Integer wfwfid = params.getInteger("wfwfid");
            List<Integer> wfwfids = Lists.newArrayList();
            wfwfids.add(wfwfid);
            classifies = classifyQueryService.listByFids(wfwfids);
        }

        JSONObject jsonObject = new JSONObject();
        JSONArray activityClassifyJsonArray = new JSONArray();
        jsonObject.put("classifies", activityClassifyJsonArray);
        if (CollectionUtils.isNotEmpty(classifies)) {
            for (Classify classify : classifies) {
                JSONObject item = new JSONObject();
                item.put("id", classify.getId());
                item.put("name", classify.getName());
                activityClassifyJsonArray.add(item);
            }
        }
        return RestRespDTO.success(jsonObject);
    }

    /**我的活动(目前只查询报名的活动数据)列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:58:31
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("my")
    public RestRespDTO listMyActivities(@RequestBody String data) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        Integer uid = params.getInteger("uid");
        String sw = params.getString("sw");
        Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
        Integer pageNum = params.getInteger("pageNum");
        pageNum = Optional.ofNullable(pageNum).orElse(1);
        Integer pageSize = params.getInteger("pageSize");
        pageSize = Optional.ofNullable(pageSize).orElse(12);
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        // flag
        String flag = urlParams.getString("flag");
        Page<Activity> page = new Page(pageNum, pageSize);
        if (uid != null) {
            String orgName = passportApiService.getOrgName(wfwfid);
            String username = passportApiService.getUserRealName(uid);
            LoginUserDTO loginUser = LoginUserDTO.buildDefault(uid, username, wfwfid, orgName);
            page = activityQueryService.pageSignedUp(page, loginUser, sw, flag);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<Activity> records = page.getRecords();
        JSONArray activityJsonArray = packageActivities(records);
        jsonObject.put("results", activityJsonArray);
        return RestRespDTO.success(jsonObject);
    }

    /**从signStat报名时间获取报名的进行状态
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:59:31
    * @param signStat
    * @return java.lang.String
    */
    private String getSignUpStatus(SignStatDTO signStat) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = signStat.getSignUpStartTime();
        LocalDateTime endTime = signStat.getSignUpEndTime();
        if (startTime.isAfter(now)) {
            return "报名未开始";
        }
        if (now.isAfter(endTime)) {
            return "已结束";
        }
        return "报名中";
    }

    private JSONObject buildField(String key, Object value, Integer flag) {
        JSONObject field = new JSONObject();
        field.put("key", key);
        field.put("value", value);
        field.put("flag", flag);
        return field;
    }

}
