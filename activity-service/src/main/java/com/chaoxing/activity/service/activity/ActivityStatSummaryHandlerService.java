package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ActivityStatSummaryMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityStatSummary;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.ActivityStatSummaryQueueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 4:12 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityStatSummaryHandlerService {

    @Resource
    private SignApiService signApiService;

    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private ActivityStatSummaryQueueService activityStatSummaryQueueService;

    @Autowired
    private ActivityStatSummaryMapper activityStatSummaryMapper;

    /**根据activityId进行对应的数据报名签到数据汇总计算
     * @Description
     * @author huxiaolong
     * @Date 2021-05-25 16:32:41
     * @param activityId
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    public void activityStatSummaryCalByActivity(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        handleActivityStatSummaryCal(activityId, activity.getSignId());
    }

    private void handleActivityStatSummaryCal(Integer activityId, Integer signId) {
        // 默认活动统计汇总
        ActivityStatSummary defaultStatSummary = ActivityStatSummary.buildDefault();
        defaultStatSummary.setActivityId(activityId);

        List<ActivityStatSummary> statSummaryList = activityStatSummaryMapper.selectList(new QueryWrapper<ActivityStatSummary>()
                .lambda()
                .eq(ActivityStatSummary::getActivityId, activityId));
        if (signId != null) {
            // 获取最新的活动统计汇总数据
            ActivityStatSummary latestStatSummary = signApiService.getActivityStatSummary(signId);

            defaultStatSummary.setSignedInNum(latestStatSummary.getSignedInNum());
            defaultStatSummary.setSignedUpNum(latestStatSummary.getSignedUpNum());
            defaultStatSummary.setSignInRate(latestStatSummary.getSignInRate());
            defaultStatSummary.setQualifiedNum(latestStatSummary.getQualifiedNum());
            defaultStatSummary.setAvgParticipateTimeLength(latestStatSummary.getAvgParticipateTimeLength());
        }
        if (CollectionUtils.isEmpty(statSummaryList)) {
            activityStatSummaryMapper.insert(defaultStatSummary);
        } else {
            activityStatSummaryMapper.update(defaultStatSummary, new UpdateWrapper<ActivityStatSummary>()
                    .lambda()
                    .eq(ActivityStatSummary::getActivityId, defaultStatSummary.getActivityId()));
        }
    }

    /**针对所有活动，对其活动统计记录进行计算新增或更新
    * @Description
    * @author huxiaolong
    * @Date 2021-05-27 17:07:27
    * @param
    * @return void
    */
    public void addOrUpdateAllActivityStatSummary() {
        List<Integer> activityIds = activityQueryService.list().stream().map(Activity::getId).collect(Collectors.toList());
        for (Integer activityId : activityIds) {
            activityStatSummaryQueueService.addSignInStat(activityId);
        }
    }
}
