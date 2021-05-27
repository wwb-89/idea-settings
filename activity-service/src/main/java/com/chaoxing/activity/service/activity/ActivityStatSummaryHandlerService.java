package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ActivityStatSummaryMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityStatSummary;
import com.chaoxing.activity.service.manager.module.SignApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Autowired
    private ActivityStatSummaryMapper activityStatSummaryMapper;

    /**根据signId进行对应的数据报名签到数据汇总计算
    * @Description
    * @author huxiaolong
    * @Date 2021-05-25 16:32:41
    * @param signId
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void activityStatSummaryCalBySign(Integer signId) {
        Activity activity = activityQueryService.getBySignId(signId);
        handleActivityStatSummaryCal(activity.getId(), signId);
    }

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
        ActivityStatSummary statSummary = activityStatSummaryMapper.selectOne(new QueryWrapper<ActivityStatSummary>()
                .lambda()
                .eq(ActivityStatSummary::getActivityId, activityId));
        Integer signedInNums = signApiService.getActivitySignedInNums(signId);
        BigDecimal signInRate = signApiService.getActivitySignInRate(signId);
        Integer qualifiedNums = signApiService.getActivityQualifiedNums(signId);
        Integer avgParticipateTimeLength = signApiService.getActivityAvgParticipateTimeLength(signId);
        boolean isNew = Boolean.FALSE;
        if (statSummary == null) {
            isNew = Boolean.TRUE;
            statSummary = ActivityStatSummary.builder()
                    .activityId(activityId)
                    .createTime(LocalDateTime.now()).build();
        }
        statSummary.setSignedInNum(signedInNums);
        statSummary.setSignInRate(signInRate);
        statSummary.setQualifiedNum(qualifiedNums);
        statSummary.setAvgParticipateInLength(avgParticipateTimeLength);
        statSummary.setUpdateTime(LocalDateTime.now());
        if (isNew) {
            activityStatSummaryMapper.insert(statSummary);
        } else {
            activityStatSummaryMapper.update(statSummary, new UpdateWrapper<ActivityStatSummary>()
                    .lambda()
                    .eq(ActivityStatSummary::getActivityId, statSummary.getActivityId()));
        }
    }
}
