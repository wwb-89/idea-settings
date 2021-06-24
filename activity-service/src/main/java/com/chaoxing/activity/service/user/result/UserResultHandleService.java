package com.chaoxing.activity.service.user.result;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.chaoxing.activity.model.UserActionRecord;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.service.user.action.UserActionRecordQueryService;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**用户成绩处理服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultHandleService
 * @description
 * @blame wwb
 * @date 2021-06-24 15:04:22
 */
@Slf4j
@Service
public class UserResultHandleService {

    @Resource
    private UserResultMapper userResultMapper;
    @Resource
    private UserResultQueryService userResultQueryService;
    @Resource
    private UserActionRecordQueryService userActionRecordQueryService;
    @Resource
    private InspectionConfigQueryService inspectionConfigQueryService;

    /**更新用户成绩
     * @Description 
     * @author wwb
     * @Date 2021-06-24 15:11:30
     * @param uid
     * @param activityId
     * @return void
    */
    public void updateUserResult(Integer uid, Integer activityId) {
        BigDecimal score = calUserActivityScore(uid, activityId);
        UserResult existUserResult = userResultQueryService.getUserResult(uid, activityId);
        if (existUserResult == null) {
            UserResult userResult = UserResult.builder()
                    .uid(uid)
                    .activityId(activityId)
                    .totalScore(score)
                    .build();
            addUserResult(userResult);
        } else {
            userResultMapper.update(null, new UpdateWrapper<UserResult>()
                    .lambda()
                    .eq(UserResult::getUid, uid)
                    .eq(UserResult::getActivityId, activityId)
                    .set(UserResult::getTotalScore, score)
            );
        }
    }

    /**新增用户成绩
     * @Description 
     * @author wwb
     * @Date 2021-06-24 15:09:39
     * @param userResult
     * @return void
    */
    public void addUserResult(UserResult userResult) {
        userResultMapper.insert(userResult);
    }

    /**计算用户成绩
     * @Description 
     * @author wwb
     * @Date 2021-06-24 15:13:29
     * @param uid
     * @param activityId
     * @return java.math.BigDecimal
    */
    private BigDecimal calUserActivityScore(Integer uid, Integer activityId) {
        // 查询考核配置
        List<InspectionConfigDetail> inspectionConfigDetails = inspectionConfigQueryService.listDetailByActivityId(activityId);
        if (CollectionUtils.isEmpty(inspectionConfigDetails)) {
            // 没有配置考核计划就得0分
            return BigDecimal.ZERO;
        }
        // 用户有效的行为记录列表
        List<UserActionRecord> userValidActionRecords = userActionRecordQueryService.listUserValidActionRecord(uid, activityId);
        if (CollectionUtils.isEmpty(userValidActionRecords)) {
            return BigDecimal.ZERO;
        }
        Map<String, BigDecimal> actionScoreMap = Maps.newHashMap();
        for (UserActionRecord userValidActionRecord : userValidActionRecords) {

        }
        // TODO
        return BigDecimal.ZERO;
    }

}