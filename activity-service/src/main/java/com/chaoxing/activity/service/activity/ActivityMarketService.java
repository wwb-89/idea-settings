package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.mapper.MarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.model.Market;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

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
    @Autowired
    private MarketMapper marketMapper;

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
    * @Date 2021-08-12 15:53:04
    * @param activity
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void shareActivityToFids(Activity activity, List<Integer> sharedFids, OperateUserDTO operateUser) {
        add(activity);
        Market originMarket = marketMapper.selectById(activity.getMarketId());
        List<Integer> marketIds = Lists.newArrayList();
        sharedFids.forEach(v -> {
            Market currMarket = marketMapper.selectList(new QueryWrapper<Market>()
                    .lambda()
                    .eq(Market::getName, originMarket.getName())
                    .eq(Market::getDeleted, Boolean.FALSE)
                    .eq(Market::getFid, v)).stream().findFirst().orElse(null);
            if (currMarket == null) {
                currMarket = Market.cloneMarket(originMarket, v);
                currMarket.perfectCreator(operateUser);
                marketMapper.insert(currMarket);
            }
            marketIds.add(currMarket.getId());
        });

        batchAddActivityMarkets(activity, marketIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchAddActivityMarkets(Activity activity, List<Integer> marketIds) {
        if (CollectionUtils.isEmpty(marketIds)) {
            return;
        }
        List<ActivityMarket> activityMarkets = Lists.newArrayList();
        marketIds.forEach(v -> {
            activityMarkets.add(ActivityMarket.builder()
                    .activityId(activity.getId())
                    .marketId(v)
                    .released(activity.getReleased())
                    .status(activity.getStatus())
                    .top(Boolean.FALSE)
                    .build());
        });
        activityMarketMapper.batchAdd(activityMarkets);
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

    /**isCreateMarket市场id和活动市场id是否一致，isCreateMarket为true，则更新所有，否则仅更新当前市场活动为删除状态
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-10 17:49:17
    * @param activityId
    * @param marketId
    * @param isCreateMarket
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Integer activityId, Integer marketId, boolean isCreateMarket) {
        activityMarketMapper.update(null, new UpdateWrapper<ActivityMarket>()
                .lambda()
                .eq(ActivityMarket::getActivityId, activityId)
                .eq(!isCreateMarket, ActivityMarket::getMarketId, marketId)
                .set(ActivityMarket::getStatus, Activity.StatusEnum.DELETED.getValue())
        );
    }
}
