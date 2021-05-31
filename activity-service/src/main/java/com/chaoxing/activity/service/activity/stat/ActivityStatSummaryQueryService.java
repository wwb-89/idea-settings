package com.chaoxing.activity.service.activity.stat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
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
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
    */
    public Page<ActivityStatSummaryDTO> activityStatSummaryPage(Page<ActivityStatSummaryDTO> page, ActivityStatSummaryQueryDTO queryParam) {

        Integer fid = queryParam.getFid();
        String activityName = queryParam.getActivityName();
        String startTime = queryParam.getStartTime();
        String endTime = queryParam.getEndTime();
        Integer orderFieldId = queryParam.getOrderFieldId();
        String orderType = queryParam.getOrderType() == null ? null : queryParam.getOrderType().getValue();
        List<Integer> externalIds = queryParam.getExternalIds();
        List<Integer> searchSignIds = signApiService.listSignIdsByExternalIds(externalIds);
        String orderField = null;
        if (orderFieldId != null) {
            TableFieldDetail field = tableFieldDetailMapper.selectById(orderFieldId);
            orderField = field == null ? null : field.getCode();
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

        packagePageData(page);
        return page;
    }

    /**对page里面的数据进行封装和转换
    * @Description
    * @author huxiaolong
    * @Date 2021-05-31 10:33:53
    * @param page
    * @return void
    */
    private void packagePageData(Page<ActivityStatSummaryDTO> page) {
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
    }
}
