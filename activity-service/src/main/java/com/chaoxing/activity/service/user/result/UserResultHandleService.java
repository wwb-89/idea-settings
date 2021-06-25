package com.chaoxing.activity.service.user.result;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.chaoxing.activity.model.UserActionRecord;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.service.user.action.UserActionRecordQueryService;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Map<String, InspectionConfigDetail> actionConfigMap = inspectionConfigDetails.stream().collect(Collectors.toMap(InspectionConfigDetail::getAction, v -> v, (v1, v2) -> v2));
        Map<String, BigDecimal> actionScoreConfigMap = actionScoreConfigMap(inspectionConfigDetails);
        // 用户有效的行为记录列表
        List<UserActionRecord> userValidActionRecords = userActionRecordQueryService.listUserValidActionRecord(uid, activityId);
        if (CollectionUtils.isEmpty(userValidActionRecords)) {
            return BigDecimal.ZERO;
        }
        // 所有行为的总得分
        Map<String, BigDecimal> actionTotalScoreMap = Maps.newHashMap();
        for (UserActionRecord userValidActionRecord : userValidActionRecords) {
            String action = userValidActionRecord.getAction();
            UserActionEnum userActionEnum = UserActionEnum.fromValue(action);
            if (userActionEnum == null) {
                continue;
            }
            BigDecimal score = actionScoreConfigMap.get(action);
            BigDecimal originScore = actionTotalScoreMap.get(action);
            originScore = Optional.ofNullable(originScore).orElse(BigDecimal.ZERO);
            actionTotalScoreMap.put(action, originScore.add(score));
        }
        // 计算总得分
        BigDecimal totalScore = BigDecimal.ZERO;
        for (InspectionConfigDetail inspectionConfigDetail : inspectionConfigDetails) {
            String action = inspectionConfigDetail.getAction();
            UserActionEnum userActionEnum = UserActionEnum.fromValue(action);
            BigDecimal upperLimit = inspectionConfigDetail.getUpperLimit();
            switch (userActionEnum) {
                case SIGNED_UP:
                    totalScore = totalScore.add(actionTotalScore(UserActionEnum.SIGNED_UP, UserActionEnum.CANCEL_SIGNED_UP, actionTotalScoreMap, upperLimit));
                    break;
                case SIGNED_IN:
                    totalScore = totalScore.add(actionTotalScore(UserActionEnum.SIGNED_IN, UserActionEnum.CANCEL_SIGNED_IN, actionTotalScoreMap, upperLimit));
                    break;
                case RATING:
                    totalScore = totalScore.add(actionTotalScore(UserActionEnum.RATING, UserActionEnum.DELETE_RATING, actionTotalScoreMap, upperLimit));
                    break;
                case PUBLISH_TOPIC:
                    totalScore = totalScore.add(actionTotalScore(UserActionEnum.PUBLISH_TOPIC, UserActionEnum.DELETE_TOPIC, actionTotalScoreMap, upperLimit));
                    break;
                case REPLY_TOPIC:
                    totalScore = totalScore.add(actionTotalScore(UserActionEnum.REPLY_TOPIC, UserActionEnum.DELETE_REPLY, actionTotalScoreMap, upperLimit));
                    break;
                case SUBMIT_WORK:
                    totalScore = totalScore.add(actionTotalScore(UserActionEnum.SUBMIT_WORK, UserActionEnum.DELETE_WORK, actionTotalScoreMap, upperLimit));
                    break;
                default:
            }
        }
        return totalScore;
    }

    private BigDecimal actionTotalScore(UserActionEnum action, UserActionEnum reverseAction, Map<String, BigDecimal> actionTotalScoreMap, BigDecimal upperLimit) {
        BigDecimal score = actionTotalScoreMap.get(action.getValue());
        score = Optional.ofNullable(score).orElse(BigDecimal.ZERO);
        BigDecimal reverseScore = actionTotalScoreMap.get(reverseAction.getValue());
        reverseScore = Optional.ofNullable(reverseScore).orElse(BigDecimal.ZERO);
        BigDecimal totalScore = score.add(reverseScore);
        if (upperLimit != null) {
            totalScore = totalScore.compareTo(upperLimit) > 0 ? upperLimit : totalScore;
        }
        return totalScore;
    }

    public Map<String, BigDecimal> actionScoreConfigMap(List<InspectionConfigDetail> inspectionConfigDetails) {
        Map<String, BigDecimal> actionScoreConfigMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(inspectionConfigDetails)) {
            for (InspectionConfigDetail inspectionConfigDetail : inspectionConfigDetails) {
                String action = inspectionConfigDetail.getAction();
                UserActionEnum userActionEnum = UserActionEnum.fromValue(action);
                if (userActionEnum == null) {
                    continue;
                }
                BigDecimal score = inspectionConfigDetail.getScore();
                BigDecimal reverseScore = BigDecimal.ZERO.subtract(score);
                score = Optional.ofNullable(score).orElse(BigDecimal.ZERO);
                switch (userActionEnum) {
                    case SIGNED_UP:
                        // 报名
                        actionScoreConfigMap.put(action, score);
                        // 取消报名
                        actionScoreConfigMap.put(UserActionEnum.CANCEL_SIGNED_UP.getValue(), reverseScore);
                        break;
                    case SIGNED_IN:
                        // 签到
                        actionScoreConfigMap.put(action, score);
                        // 取消签到
                        actionScoreConfigMap.put(UserActionEnum.CANCEL_SIGNED_IN.getValue(), reverseScore);
                        break;
                    case RATING:
                        // 评价
                        actionScoreConfigMap.put(action, score);
                        // 删除评价
                        actionScoreConfigMap.put(UserActionEnum.DELETE_RATING.getValue(), reverseScore);
                        break;
                    case PUBLISH_TOPIC:
                        // 发帖
                        actionScoreConfigMap.put(action, score);
                        // 删除发帖
                        actionScoreConfigMap.put(UserActionEnum.DELETE_TOPIC.getValue(), reverseScore);
                        break;
                    case REPLY_TOPIC:
                        // 回帖
                        actionScoreConfigMap.put(action, score);
                        // 删除回帖
                        actionScoreConfigMap.put(UserActionEnum.DELETE_REPLY.getValue(), reverseScore);
                        break;
                    case SUBMIT_WORK:
                        // 提交作品
                        actionScoreConfigMap.put(action, score);
                        // 删除作品
                        actionScoreConfigMap.put(UserActionEnum.DELETE_WORK.getValue(), reverseScore);
                        break;
                    default:
                }
            }
        }
        return actionScoreConfigMap;
    }


    /**改变用户合格状态
     * @Description
     * @author huxiaolong
     * @Date 2021-06-24 18:29:20
     * @param activityId
     * @param uid
     * @param qualifiedStatusEnum
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateQualifiedStatus(Integer activityId, Integer uid, UserResult.QualifiedStatusEnum qualifiedStatusEnum) {
        userResultMapper.update(null, new UpdateWrapper<UserResult>()
                .lambda()
                .eq(UserResult::getActivityId, activityId)
                .eq(UserResult::getUid, uid)
                .set(UserResult::getQualifiedStatus, qualifiedStatusEnum.getValue())
                .set(UserResult::getManualQualifiedStatus, qualifiedStatusEnum.getValue()));
    }

    /**批量改变用户合格状态
     * @Description
     * @author huxiaolong
     * @Date 2021-06-24 18:29:08
     * @param activityId
     * @param uids
     * @param qualifiedStatusEnum
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateQualifiedStatus(Integer activityId, String uids, UserResult.QualifiedStatusEnum qualifiedStatusEnum) {
        if (StringUtils.isNotBlank(uids)) {
            return;
        }
        List<Integer> uidList = JSON.parseArray(uids, Integer.class);
        if (CollectionUtils.isEmpty(uidList)) {
            return;
        }
        userResultMapper.update(null, new UpdateWrapper<UserResult>()
                .lambda()
                .eq(UserResult::getActivityId, activityId)
                .in(UserResult::getUid, uidList)
                .set(UserResult::getQualifiedStatus, qualifiedStatusEnum.getValue())
                .set(UserResult::getManualQualifiedStatus, qualifiedStatusEnum.getValue()));


    }
}