package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMarket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/10 15:06
 * <p>
 */
@Slf4j
@Service
public class ActivityMarketService {

    @Autowired
    private ActivityMarketMapper activityMarketMapper;

    /***
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-10 17:49:25
    * @param activity
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void add(Activity activity) {
        if (activity.getMarketId() == null) {
            return;
        }
        activityMarketMapper.insert(ActivityMarket.builder()
                .activityId(activity.getId())
                .marketId(activity.getMarketId())
                .released(activity.getReleased())
                .status(activity.getStatus())
                .top(Boolean.FALSE)
                .build());
    }

    /**
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-10 17:49:33
    * @param activityId
    * @param marketId
    * @param isTop
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityTop(Integer activityId, Integer marketId, Boolean isTop) {
        activityMarketMapper.update(null, new UpdateWrapper<ActivityMarket>()
                .lambda()
                .eq(ActivityMarket::getActivityId, activityId)
                .eq(ActivityMarket::getMarketId, marketId)
                .set(ActivityMarket::getTop, isTop)
        );
    }
    
    /**
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-10 17:48:51
    * @param marketId
    * @param activity
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void updateMarketActivityStatus(Integer marketId, Activity activity) {
        Activity.StatusEnum status = Activity.calActivityStatus(activity);
        activityMarketMapper.update(null, new UpdateWrapper<ActivityMarket>()
                .lambda()
                .eq(ActivityMarket::getActivityId, activity.getId())
                .eq(ActivityMarket::getMarketId, marketId)
                .set(ActivityMarket::getReleased, activity.getReleased())
                .set(ActivityMarket::getStatus, status.getValue())
        );
    }

    /**
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-10 17:49:17
    * @param activityId
    * @param marketId
    * @param removeAll
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Integer activityId, Integer marketId, boolean removeAll) {
        if (removeAll) {
            activityMarketMapper.delete(new QueryWrapper<ActivityMarket>().lambda().eq(ActivityMarket::getActivityId, activityId));
            return;
        }
        activityMarketMapper.delete(new QueryWrapper<ActivityMarket>().lambda().eq(ActivityMarket::getActivityId, activityId).eq(ActivityMarket::getMarketId, marketId));
    }
}
