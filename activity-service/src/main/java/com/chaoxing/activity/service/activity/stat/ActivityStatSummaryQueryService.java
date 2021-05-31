package com.chaoxing.activity.service.activity.stat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.sign.SignParticipateScopeDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.mapper.ActivityRatingMapper;
import com.chaoxing.activity.mapper.ActivityStatSummaryMapper;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.enums.OrderTypeEnum;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 1:53 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityStatSummaryQueryService {

    @Autowired
    private ActivityClassifyMapper activityClassifyMapper;

    @Autowired
    private ActivityRatingMapper activityRatingMapper;

    @Autowired
    private ActivityStatSummaryMapper activityStatSummaryMapper;

    @Autowired
    private TableFieldDetailMapper tableFieldDetailMapper;

    @Autowired
    private SignApiService signApiService;

    /**对活动统计汇总进行分页查询
    * @Description
    * @author huxiaolong
    * @Date 2021-05-25 16:32:27
    * @param page
    * @param fid
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
    */
    public Page<ActivityStatSummaryDTO> activityStatSummaryPage(Page<ActivityStatSummaryDTO> page, Integer fid) {
        return activityStatSummaryPage(page, fid, null);
    }

    /**对活动统计汇总进行分页查询
    * @Description
    * @author huxiaolong
    * @Date 2021-05-25 16:32:27
    * @param page
    * @param fid
    * @param queryParamStr
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
    */
    public Page<ActivityStatSummaryDTO> activityStatSummaryPage(Page<ActivityStatSummaryDTO> page, Integer fid, String queryParamStr) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(queryParamStr)) {
            jsonObject = JSON.parseObject(queryParamStr);
        }
        String activityName = jsonObject.getString("activityName");
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        Integer orderFieldId = jsonObject.getInteger("orderFieldId");
        String externalIdsStr = jsonObject.getString("externalIds");
        List<Integer> externalIds = new ArrayList<>();
        List<Integer> searchSignIds = new ArrayList<>();
        if (StringUtils.isNotBlank(externalIdsStr)) {
            externalIds = JSON.parseArray(externalIdsStr, Integer.class);
            searchSignIds = signApiService.listSignIdsByExternalIds(externalIds);
        }
        String orderType = jsonObject.getString("orderType");
        String orderField = null;
        if (orderFieldId != null) {
            TableFieldDetail field = tableFieldDetailMapper.selectById(orderFieldId);
            if (field != null) {
                orderField = field.getCode();
            }
        }
        if (orderType != null) {
            OrderTypeEnum typeEnum = OrderTypeEnum.fromValue(orderType);
            if (typeEnum == null) {
                orderType = null;
            }
        }

        // 传递了参与范围的组织架构id集，报名签到id却为空，则返回空
        if (CollectionUtils.isNotEmpty(externalIds) && CollectionUtils.isEmpty(searchSignIds)) {
            return page;
        }
        page = activityStatSummaryMapper.activityStatSummaryPage(page, fid, activityName, startTime, endTime, orderField,
                orderType, searchSignIds);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return page;
        }
        List<Integer> activityIds = Lists.newArrayList();
        List<Integer> signIds = Lists.newArrayList();
        Set<Integer> classifyIds = Sets.newHashSet();
        for (ActivityStatSummaryDTO record : page.getRecords()) {
            activityIds.add(record.getActivityId());
            if (record.getSignId() != null) {
                signIds.add(record.getSignId());
            }
            if (record.getActivityClassifyId() != null) {
                classifyIds.add(record.getActivityClassifyId());
            }
        }

        List<SignParticipateScopeDTO> signParticipateScopes = signApiService.listSignParticipateScopeBySignIds(signIds);
        Map<Integer, String> signParticipateScopeMap = new HashMap<>();
        for (SignParticipateScopeDTO item : signParticipateScopes) {
            signParticipateScopeMap.put(item.getSignId(), item.getExternalName());
        }


        Map<Integer, String> classifyMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(classifyIds)) {
            List<ActivityClassify> classifies = activityClassifyMapper.listByIds(classifyIds);
            classifyMap = classifies.stream().collect(Collectors.toMap(ActivityClassify::getId, ActivityClassify::getName, (v1, v2) -> v2));
        }
        List<ActivityRating> ratings = activityRatingMapper.selectList(new QueryWrapper<ActivityRating>()
                .lambda()
                .in(ActivityRating::getActivityId, activityIds));
        Map<Integer, Integer> ratingMap = ratings.stream().collect(Collectors.toMap(ActivityRating::getActivityId, ActivityRating::getScoreNum, (v1, v2) -> v2));

        for (ActivityStatSummaryDTO record : page.getRecords()) {
            Integer classifyId = record.getActivityClassifyId();
            Integer signId = record.getSignId();
            if (classifyId != null) {
                record.setActivityClassify(classifyMap.get(classifyId));
            }
            if (signId != null) {
                record.setParticipateScope(signParticipateScopeMap.get(signId));
            }
            Integer ratingNum = Optional.ofNullable(ratingMap.get(record.getActivityId())).orElse(0);
            record.setRateNum(ratingNum);
        }

        return page;
    }
}
