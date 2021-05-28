package com.chaoxing.activity.service.activity.stat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.mapper.ActivityRatingMapper;
import com.chaoxing.activity.mapper.ActivityStatSummaryMapper;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.util.enums.OrderTypeEnum;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

        page = activityStatSummaryMapper.activityStatSummaryPage(page, fid, activityName, startTime, endTime, orderField, orderType);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return page;
        }
        List<Integer> activityIds = Lists.newArrayList();
        Set<Integer> classifyIds = Sets.newHashSet();
        for (ActivityStatSummaryDTO record : page.getRecords()) {
            activityIds.add(record.getActivityId());
            classifyIds.add(record.getActivityClassifyId());
        }

        Map<Integer, String> classifyMap = Maps.newHashMap();
        Map<Integer, Integer> ratingMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(classifyIds)) {
            List<ActivityClassify> classifies = activityClassifyMapper.selectBatchIds(classifyIds);
            classifies.forEach(classify -> {
                classifyMap.put(classify.getId(), classify.getName());
            });
        }
        List<ActivityRating> ratings = activityRatingMapper.selectBatchIds(activityIds);
        ratings.forEach(rating -> {
            ratingMap.put(rating.getActivityId(), rating.getScoreNum());
        });

        page.getRecords().forEach(record -> {
            Integer classifyId = record.getActivityClassifyId();
            if (classifyId != null) {
                record.setActivityClassify(classifyMap.get(classifyId));
            }
            Integer ratingNum = Optional.ofNullable(ratingMap.get(record.getActivityId())).orElse(0);
            record.setRateNum(ratingNum);
        });

        return page;
    }
}
