package com.chaoxing.activity.service.activity.stat;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.mapper.ActivityStatSummaryMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Resource
    private ActivityQueryService activityQueryService;

    @Autowired
    private ActivityStatSummaryMapper activityStatSummaryMapper;

    /**对活动统计汇总进行分页查询
    * @Description
    * @author huxiaolong
    * @Date 2021-05-25 16:32:27
    * @param page
    * @param fid
    * @param startTimeStr
    * @param endTimeStr
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
    */
    public Page<ActivityStatSummaryDTO> activityStatSummaryPage(Page<ActivityStatSummaryDTO> page, Integer fid, String startTimeStr, String endTimeStr) {
        Page<Activity> activityPage = new Page<>(page.getCurrent(), page.getSize());
        activityPage = activityQueryService.activityPage(activityPage, fid, startTimeStr, endTimeStr);
        BeanUtils.copyProperties(activityPage, page);
        List<Activity> activities = activityPage.getRecords();
        if (CollectionUtils.isEmpty(activities)) {
            return page;
        }

        List<ActivityStatSummaryDTO> records = new ArrayList<>();
        List<Integer> activityIds = activities.stream().map(Activity::getId).collect(Collectors.toList());
        List<ActivityStatSummary> statSummaries = activityStatSummaryMapper.selectBatchIds(activityIds);
        Map<Integer, ActivityStatSummary> statSummaryMap = Maps.newHashMap();
        for (ActivityStatSummary statSummary : statSummaries) {
            statSummaryMap.put(statSummary.getActivityId(), statSummary);
        }
        for (Activity activity : activities) {
            Integer activityId = activity.getId();
            ActivityStatSummaryDTO item = new ActivityStatSummaryDTO();
            item.setActivityId(activityId);
            item.setActivityStatus(activity.getStatus());
            item.setActivityName(activity.getName());
            item.setActivityCreator(activity.getCreateUserName());
            item.setActivityCreateUid(activity.getCreateUid());
            item.setIntegral(activity.getIntegralValue());
            item.setStartTime(activity.getStartTime());
            item.setEndTime(activity.getEndTime());

            ActivityStatSummary statSummary = statSummaryMap.get(activityId);
            if (statSummary != null) {
                item.setSignedInNum(statSummary.getSignedInNum());
                item.setSignInRate(statSummary.getSignInRate());
                item.setQualifiedNum(statSummary.getQualifiedNum());
                item.setCreateTime(statSummary.getCreateTime());
                item.setUpdateTime(statSummary.getUpdateTime());
            }
            records.add(item);
        }
        page.setRecords(records);
        return page;
    }
}
