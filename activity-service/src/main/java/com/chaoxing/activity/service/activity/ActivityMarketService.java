package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.queue.OrgAssociateActivityQueue;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Resource
    private ActivityMarketMapper activityMarketMapper;

    @Resource
    private OrgAssociateActivityQueue orgAssociateActivityQueue;
    @Resource
    private MarketHandleService marketHandleService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private ActivityHandleService activityHandleService;

    /***关联
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-10 17:49:25
    * @param activity
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void associate(Activity activity) {
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

    /**根据活动id和市场id查询
     * @Description 
     * @author wwb
     * @Date 2021-09-14 15:50:49
     * @param activityId
     * @param marketId
     * @return com.chaoxing.activity.model.ActivityMarket
    */
    public ActivityMarket get(Integer activityId, Integer marketId) {
        List<ActivityMarket> activityMarkets = activityMarketMapper.selectList(new LambdaQueryWrapper<ActivityMarket>()
                .eq(ActivityMarket::getActivityId, activityId)
                .eq(ActivityMarket::getMarketId, marketId)
        );
        return Optional.ofNullable(activityMarkets).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**活动关联活动市场
     * @Description 
     * @author wwb
     * @Date 2021-09-14 15:35:34
     * @param activity
     * @param marketId
     * @return void
    */
    public void associate(Activity activity, Integer marketId) {
        if (marketId == null) {
            return;
        }
        Integer activityId = activity.getId();
        Boolean released = false;
        Activity.StatusEnum statusEnum = Activity.calAndSetActivityStatus(activity.getStartTime(), activity.getEndTime(), released);
        // 查询是否已经有关联
        ActivityMarket activityMarket = get(activityId, marketId);
        if (activityMarket == null) {
            activityMarketMapper.insert(ActivityMarket.builder()
                    .activityId(activityId)
                    .marketId(marketId)
                    .released(released)
                    .status(statusEnum.getValue())
                    .top(false)
                    .build());
        }
    }

    /**机构关联活动
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-12 15:53:04
    * @param activity
    * @return void
    */
    public void shareActivityToFids(Activity activity, List<Integer> sharedFids, OperateUserDTO operateUser) {
        if (CollectionUtils.isNotEmpty(sharedFids)) {
            for (Integer sharedFid : sharedFids) {
                orgAssociateActivityQueue.push(new OrgAssociateActivityQueue.QueueParamDTO(activity.getId(), sharedFid, operateUser.getUid()));
            }
        }
    }

    /**机构关联活动
     * @Description 
     * @author wwb
     * @Date 2021-09-14 15:30:22
     * @param fid
     * @param activityId
     * @param uid
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void orgAssociatedActivity(Integer fid, Integer activityId, Integer uid) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        String activityFlag = activity.getActivityFlag();
        PassportUserDTO passportUserDto = passportApiService.getByUid(uid);
        String userName = Optional.ofNullable(passportUserDto).map(PassportUserDTO::getRealName).orElse("");
        String orgName = passportApiService.getOrgName(fid);
        Integer marketId = marketHandleService.getOrCreateOrgMarket(fid, Activity.ActivityFlagEnum.fromValue(activityFlag), LoginUserDTO.buildDefault(uid, userName, fid, orgName));
        // 关联
        associate(activity, marketId);
    }

    /**活动置顶
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

    /**更新活动发布状态
     * @Description 
     * @author wwb
     * @Date 2021-09-22 17:05:01
     * @param marketId
     * @param activityId
     * @param release
     * @return void
    */
    public void updateActivityRelease(Integer marketId, Integer activityId, boolean release) {
        Activity activity = activityQueryService.getById(activityId);
        updateActivityRelease(marketId, activity, release);
    }

    /**更新活动发布状态
     * @Description 
     * @author wwb
     * @Date 2021-09-22 17:08:38
     * @param marketId
     * @param activity
     * @param release
     * @return void
    */
    public void updateActivityRelease(Integer marketId, Activity activity, boolean release) {
        Integer activityId = activity.getId();
        ActivityMarket activityMarket = get(activityId, marketId);
        if (activityMarket == null) {
            return;
        }
        Activity.StatusEnum status = Activity.calAndSetActivityStatus(activity.getStartTime(), activity.getEndTime(), release);
        activityMarketMapper.update(null, new UpdateWrapper<ActivityMarket>()
                .lambda()
                .eq(ActivityMarket::getActivityId, activityId)
                .eq(ActivityMarket::getMarketId, marketId)
                .set(ActivityMarket::getReleased, release)
                .set(ActivityMarket::getStatus, status.getValue())
        );
    }

    /**更新活动状态
     * @Description 
     * @author wwb
     * @Date 2021-09-22 17:20:05
     * @param marketId
     * @param activity
     * @return void
    */
    public void updateActivityStatus(Integer marketId, Activity activity) {
        Integer activityId = activity.getId();
        ActivityMarket activityMarket = get(activityId, marketId);
        if (activityMarket == null) {
            return;
        }
        Boolean released = activityMarket.getReleased();
        Activity.StatusEnum status = Activity.calAndSetActivityStatus(activity.getStartTime(), activity.getEndTime(), released);
        activityMarketMapper.update(null, new UpdateWrapper<ActivityMarket>()
                .lambda()
                .eq(ActivityMarket::getActivityId, activityId)
                .eq(ActivityMarket::getMarketId, marketId)
                .set(ActivityMarket::getStatus, status.getValue())
        );
    }

    /**发布活动
     * @Description 
     * @author wwb
     * @Date 2021-09-22 17:08:57
     * @param marketId
     * @param activityId
     * @param loginUser
     * @return void
    */
    public void releaseActivity(Integer marketId, Integer activityId, LoginUserDTO loginUser) {
        ActivityMarket activityMarket = get(activityId, marketId);
        if (activityMarket == null) {
            return;
        }
        Activity activity = activityQueryService.getById(activityId);
        if (Objects.equals(marketId, activity.getMarketId())) {
            // 委托活动去修改发布状态
            activityHandleService.release(activityId, loginUser);
        } else {
            updateActivityRelease(marketId, activity, true);
        }
    }

    /**取消发布活动
     * @Description 
     * @author wwb
     * @Date 2021-09-22 17:09:19
     * @param marketId
     * @param activityId
     * @param loginUser
     * @return void
    */
    public void cancelReleaseActivity(Integer marketId, Integer activityId, LoginUserDTO loginUser) {
        ActivityMarket activityMarket = get(activityId, marketId);
        if (activityMarket == null) {
            return;
        }
        Activity activity = activityQueryService.getById(activityId);
        if (Objects.equals(marketId, activity.getMarketId())) {
            // 委托活动去修改发布状态
            activityHandleService.cancelRelease(activityId, loginUser);
        } else {
            updateActivityRelease(marketId, activity, false);
        }
    }

    /**查询关联了活动的记录
     * @Description 
     * @author wwb
     * @Date 2021-09-22 17:42:03
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.ActivityMarket>
    */
    public List<ActivityMarket> listByActivityId(Integer activityId) {
        return activityMarketMapper.selectList(new LambdaQueryWrapper<ActivityMarket>()
                .eq(ActivityMarket::getActivityId, activityId)
                .ne(ActivityMarket::getStatus, Activity.StatusEnum.DELETED.getValue())
        );
    }

    /**删除市场id关联的活动
     * @Description
     * @author huxiaolong
     * @Date 2021-11-01 16:28:16
     * @param marketId
     * @return void
     */
    public void deleteByMarketId(Integer marketId) {
        if (marketId == null) {
            return;
        }
        activityMarketMapper.update(null, new LambdaUpdateWrapper<ActivityMarket>()
                .eq(ActivityMarket::getMarketId, marketId)
                .set(ActivityMarket::getStatus, 0));
    }
}
