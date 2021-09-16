package com.chaoxing.activity.api.controller.mh.datacenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.api.util.MhPreParamsUtils;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhMarketDataCenterDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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


    /**
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
        markets.forEach(v -> result.add(MhMarketDataCenterDTO.buildFromMarket(v)));
        return RestRespDTO.success(result);
    }

    @RequestMapping("/market/{marketId}")
    public RestRespDTO index(@RequestBody String data, @PathVariable Integer marketId) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");

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
        JSONArray activityJsonArray = new JSONArray();
        jsonObject.put("results", activityJsonArray);
        List<Activity> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            for (Activity record : records) {
                // 活动
                JSONObject activity = new JSONObject();
                activity.put("id", record.getId());
                activity.put("type", 3);
                activity.put("orsUrl", record.getPreviewUrl());
                JSONArray fields = new JSONArray();
                activity.put("fields", fields);
                // 封面
                int fieldFlag = 0;
                fields.add(buildField("封面",activityCoverUrlSyncService.getCoverUrl(record), fieldFlag));
                // 活动名称
                fields.add(buildField("活动名称",record.getName(), ++fieldFlag));
                // 开始时间
                fields.add(buildField("活动时间",DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()), ++fieldFlag));
                fields.add(buildField("活动时间",DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()), ++fieldFlag));

                String activityAddress = record.getAddress();
                if (StringUtils.isNotBlank(activityAddress)) {
                    String detailAddress = record.getDetailAddress();
                    if (StringUtils.isNotBlank(detailAddress)) {
                        activityAddress += detailAddress;
                    }
                }
                // 地点
                fields.add(buildField("活动地点",activityAddress, ++fieldFlag));
                // 活动分类
                fields.add(buildField("分类",record.getActivityClassifyName(), ++fieldFlag));
                // 主办方
                if (StringUtils.isNotBlank(record.getOrganisers())) {
                    fields.add(buildField("主办",record.getOrganisers(), ++fieldFlag));
                }
                activityJsonArray.add(activity);
            }
        }
        return RestRespDTO.success(jsonObject);
    }

    @RequestMapping("{marketId}/classifies")
    public RestRespDTO listClassify(@RequestBody String data, @PathVariable Integer marketId) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        // flag
        String flag = urlParams.getString("flag");
        if (marketId == null && StringUtils.isNotBlank(flag)) {
            // 根据flag找活动市场id
            marketId = marketQueryService.getMarketIdByTemplate(wfwfid, flag);
        }
        List<Classify> classifies = Lists.newArrayList();
        if (marketId != null) {
            classifies = classifyQueryService.listMarketClassifies(marketId);
        }/* else {
            classifies = classifyQueryService.listByFids(wfwfids);
        }*/

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

    private JSONObject buildField(String key, Object value, Integer flag) {
        JSONObject field = new JSONObject();
        field.put("key", key);
        field.put("value", value);
        field.put("flag", flag);
        return field;
    }

}
