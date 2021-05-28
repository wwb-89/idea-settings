package com.chaoxing.activity.service.stat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.sign.UserSignStatSummaryDTO;
import com.chaoxing.activity.mapper.UserStatSummaryMapper;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityStatQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.user.UserStatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**用户统计汇总服务
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryService
 * @description
 * @blame wwb
 * @date 2021-05-26 14:37:21
 */
@Slf4j
@Service
public class UserStatSummaryService {

    @Resource
    private UserStatSummaryMapper userStatSummaryMapper;

    @Resource
    private SignApiService signApiService;
    @Resource
    private ActivityStatQueryService activityStatQueryService;
    @Resource
    private UserStatService userStatService;
    @Resource
    private PassportApiService passportApiService;

    /**更新用户签到数
     * @Description 
     * @author wwb
     * @Date 2021-05-26 14:39:27
     * @param uid
     * @return void
    */
    public void updateUserSignInData(Integer uid) {
        // 查询签到数、签到率、参与时长
        UserSignStatSummaryDTO userSignStatSummary = signApiService.userSignStatSummary(uid);
        List<UserStatSummary> userStatSummaries = userStatSummaryMapper.selectList(new QueryWrapper<UserStatSummary>()
                .lambda()
                .eq(UserStatSummary::getUid, uid)
        );
        Integer participatedActivityNum = 0;
        List<Integer> participatedSignIds = userSignStatSummary.getParticipatedSignIds();
        if (CollectionUtils.isNotEmpty(participatedSignIds)) {
            participatedActivityNum = activityStatQueryService.countActivityNumBySignIds(participatedSignIds);
        }
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            // 更新用户数据
            userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
                .lambda()
                    .eq(UserStatSummary::getUid, uid)
                    .set(UserStatSummary::getSignedInNum, userSignStatSummary.getValidSignedInNum())
                    .set(UserStatSummary::getSignedInRate, userSignStatSummary.getSignedInRate())
                    .set(UserStatSummary::getTotalParticipateTimeLength, userSignStatSummary.getParticipateTimeLength())
                    .set(UserStatSummary::getParticipateActivityNum, participatedActivityNum)
            );
        } else {
            // 新增用户数据
            UserStatSummary userStatSummary = UserStatSummary.builder()
                    .uid(uid)
                    .realName(passportApiService.getUserRealName(uid))
                    .signedInNum(userSignStatSummary.getValidSignedInNum())
                    .signedInRate(userSignStatSummary.getSignedInRate())
                    .totalParticipateTimeLength(userSignStatSummary.getParticipateTimeLength())
                    .participateActivityNum(participatedActivityNum)
                    .build();
            userStatSummaryMapper.insert(userStatSummary);
        }
    }

    /**更新用户成绩合格数
     * @Description 
     * @author wwb
     * @Date 2021-05-26 14:39:57
     * @param uid
     * @return void
    */
    public void updateUserResultData(Integer uid) {
        // 查询合格数
        Integer qualifiedResultNum = signApiService.userQualifiedResultNum(uid);
        List<UserStatSummary> userStatSummaries = userStatSummaryMapper.selectList(new QueryWrapper<UserStatSummary>()
                .lambda()
                .eq(UserStatSummary::getUid, uid)
        );
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            // 更新用户数据
            userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
                    .lambda()
                    .eq(UserStatSummary::getUid, uid)
                    .set(UserStatSummary::getQualifiedNum, qualifiedResultNum)
            );
        } else {
            // 新增用户数据
            UserStatSummary userStatSummary = UserStatSummary.builder()
                    .uid(uid)
                    .realName(passportApiService.getUserRealName(uid))
                    .qualifiedNum(qualifiedResultNum)
                    .build();
            userStatSummaryMapper.insert(userStatSummary);
        }
    }

    /**更新用户评价数
     * @Description 
     * @author wwb
     * @Date 2021-05-27 23:07:47
     * @param uid
     * @return void
    */
    public void updateUserRatingNum(Integer uid) {
        Integer ratingNum = userStatService.countUserRatingNum(uid);
        List<UserStatSummary> userStatSummaries = userStatSummaryMapper.selectList(new QueryWrapper<UserStatSummary>()
                .lambda()
                .eq(UserStatSummary::getUid, uid)
        );
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            // 更新
            userStatSummaryMapper.update(null, new UpdateWrapper<UserStatSummary>()
                    .lambda()
                    .eq(UserStatSummary::getUid, uid)
                    .set(UserStatSummary::getRatingNum, ratingNum)
            );
        } else {
            // 新增
            UserStatSummary userStatSummary = UserStatSummary.builder()
                    .uid(uid)
                    .realName(passportApiService.getUserRealName(uid))
                    .ratingNum(ratingNum)
                    .build();
            userStatSummaryMapper.insert(userStatSummary);
        }
    }

}