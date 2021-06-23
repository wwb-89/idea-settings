package com.chaoxing.activity.service.stat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.sign.UserSignStatSummaryDTO;
import com.chaoxing.activity.mapper.UserStatSummaryMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.user.UserStatService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**用户统计汇总服务
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryHandleService
 * @description
 * @blame wwb
 * @date 2021-05-26 14:37:21
 */
@Slf4j
@Service
public class UserStatSummaryHandleService {

    @Resource
    private UserStatSummaryMapper userStatSummaryMapper;

    @Resource
    private SignApiService signApiService;
    @Resource
    private UserStatService userStatService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private OrganizationalStructureApiService organizationalStructureApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private UserResultQueryService userResultQueryService;

    /**更新用户报名签到信息
     * @Description 
     * @author wwb
     * @Date 2021-05-26 14:39:27
     * @param uid
     * @param activityId
     * @return void
    */
    public void updateUserSignData(Integer uid, Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        Integer signId = activity.getSignId();
        if (signId == null) {
            return;
        }
        // 查询用户在报名签到下的统计信息
        UserSignStatSummaryDTO userSignStatSummary = signApiService.userSignStatSummary(uid, signId);
        List<UserStatSummary> userStatSummaries = userStatSummaryMapper.selectList(new QueryWrapper<UserStatSummary>()
                .lambda()
                .eq(UserStatSummary::getUid, uid)
                .eq(UserStatSummary::getActivityId, activityId)
        );
        // 是否有效, 报名数>0的时候报名成功数必须>0 否则签到成功数必须>0
        boolean isValid;
        if (userSignStatSummary.getSignUpNum() > 0) {
            isValid = userSignStatSummary.getSignedUpNum() > 0;
        }else {
            isValid = userSignStatSummary.getSignedInNum() > 0;
        }
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            // 更新用户数据
            userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
                .lambda()
                    .eq(UserStatSummary::getUid, uid)
                    .eq(UserStatSummary::getActivityId, activityId)
                    .set(UserStatSummary::getSignUpNum, userSignStatSummary.getSignUpNum())
                    .set(UserStatSummary::getSignedUpNum, userSignStatSummary.getSignedUpNum())
                    .set(UserStatSummary::getSignUpTime, userSignStatSummary.getSignUpTime())
                    .set(UserStatSummary::getSignInNum, userSignStatSummary.getSignInNum())
                    .set(UserStatSummary::getSignedInNum, userSignStatSummary.getSignedInNum())
                    .set(UserStatSummary::getParticipateTimeLength, userSignStatSummary.getParticipateTimeLength())
                    .set(UserStatSummary::getValid, isValid)
            );
        } else {
            // 新增用户数据
            UserStatSummary userStatSummary = buildDefault(uid, activity.getCreateFid());
            userStatSummary.setActivityId(activityId);
            userStatSummary.setSignUpNum(userSignStatSummary.getSignUpNum());
            userStatSummary.setSignedUpNum(userSignStatSummary.getSignedUpNum());
            userStatSummary.setSignUpTime(userSignStatSummary.getSignUpTime());
            userStatSummary.setSignInNum(userSignStatSummary.getSignInNum());
            userStatSummary.setSignedInNum(userSignStatSummary.getSignedInNum());
            userStatSummary.setParticipateTimeLength(userSignStatSummary.getParticipateTimeLength());
            userStatSummary.setValid(isValid);
            userStatSummaryMapper.insert(userStatSummary);
        }
    }

    private UserStatSummary buildDefault(Integer uid, Integer activityCreateFid) {
        String realName = "";
        String mobile = "";
        try {
            PassportUserDTO passportUser = passportApiService.getByUid(uid);
            realName = passportUser.getRealName();
            mobile = passportUser.getMobile();
        } catch (Exception e) {}
        String studentNo = organizationalStructureApiService.getUserStudentNo(uid, activityCreateFid);
        String userFirstGroupName = organizationalStructureApiService.getUserFirstGroupName(uid, activityCreateFid);
        return UserStatSummary.builder()
                .uid(uid)
                .realName(realName)
                .mobile(mobile)
                .studentNo(studentNo)
                .organizationStructure(userFirstGroupName)
                .build();
    }

    /**更新用户成绩合格
     * @Description 
     * @author wwb
     * @Date 2021-05-26 14:39:57
     * @param uid
     * @param activityId
     * @return void
    */
    public void updateUserResult(Integer uid, Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        Integer signId = activity.getSignId();
        if (signId == null) {
            return;
        }
        boolean isQualified = userResultQueryService.isUserQualified(uid, activityId);
        List<UserStatSummary> userStatSummaries = userStatSummaryMapper.selectList(new QueryWrapper<UserStatSummary>()
                .lambda()
                .eq(UserStatSummary::getUid, uid)
                .eq(UserStatSummary::getActivityId, activityId)
        );
        BigDecimal integral = activity.getIntegralValue();
        integral = Optional.ofNullable(integral).orElse(BigDecimal.ZERO);
        if (!isQualified) {
            integral = BigDecimal.ZERO;
        }
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            // 更新用户数据
            userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
                    .lambda()
                    .eq(UserStatSummary::getUid, uid)
                    .eq(UserStatSummary::getActivityId, activityId)
                    .set(UserStatSummary::getQualified, isQualified)
                    .set(UserStatSummary::getIntegral, integral)
            );
        } else {
            // 新增用户数据
            UserStatSummary userStatSummary = buildDefault(uid, activity.getCreateFid());
            userStatSummary.setActivityId(activityId);
            userStatSummary.setQualified(isQualified);
            userStatSummary.setIntegral(integral);
            userStatSummaryMapper.insert(userStatSummary);
        }
    }

    /**更新用户评价数
     * @Description 
     * @author wwb
     * @Date 2021-05-27 23:07:47
     * @param uid
     * @param activityId
     * @return void
    */
    public void updateUserRatingNum(Integer uid, Integer activityId) {
        Integer ratingNum = userStatService.countUserRatingNum(uid, activityId);
        List<UserStatSummary> userStatSummaries = userStatSummaryMapper.selectList(new QueryWrapper<UserStatSummary>()
                .lambda()
                .eq(UserStatSummary::getUid, uid)
                .eq(UserStatSummary::getActivityId, activityId)
        );
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            // 更新
            userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
                    .lambda()
                    .eq(UserStatSummary::getUid, uid)
                    .eq(UserStatSummary::getActivityId, activityId)
                    .set(UserStatSummary::getRatingNum, ratingNum)
            );
        } else {
            // 新增
            UserStatSummary userStatSummary = UserStatSummary.builder()
                    .uid(uid)
                    .activityId(activityId)
                    .realName(passportApiService.getUserRealName(uid))
                    .ratingNum(ratingNum)
                    .build();
            userStatSummaryMapper.insert(userStatSummary);
        }
    }

    /**更新活动下用户获得的积分
     * @Description 只有合格的才更新
     * @author wwb
     * @Date 2021-06-02 10:07:10
     * @param activityId
     * @param integral
     * @return void
    */
    public void updateActivityUserIntegral(Integer activityId, BigDecimal integral) {
        integral = Optional.ofNullable(integral).orElse(BigDecimal.ZERO);
        userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
            .lambda()
                .eq(UserStatSummary::getActivityId, activityId)
                .eq(UserStatSummary::getQualified, true)
                .set(UserStatSummary::getIntegral, integral)
        );
    }

}