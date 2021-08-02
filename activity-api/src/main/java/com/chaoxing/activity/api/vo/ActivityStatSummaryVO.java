package com.chaoxing.activity.api.vo;

import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/2 15:25
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatSummaryVO {
    /** 活动id; */
    private Integer activityId;
    /** 报名签到id; */
    private Integer signId;
    /** 活动名称; */
    private String activityName;
    /** 活动门户地址 */
    private String activityPreviewUrl;
    /** 活动状态; */
    private Integer activityStatus;
    /** 活动分类; */
    private String activityClassify;
    /** 活动分类; */
    private Integer activityClassifyId;
    /** 创建者; */
    private String activityCreator;
    /** 创建者; */
    private Integer activityCreateUid;
    /** 参与范围; */
    private String participateScope;
    /** 积分; */
    private BigDecimal integral;
    /** 开始时间; */
    private Long startTime;
    /** 结束时间; */
    private Long endTime;
    /** 评价数; */
    private Integer rateNum;
    /** 评分 */
    private BigDecimal rateScore;
    /** 签到数量; */
    private Integer signedInNum;
    /** 签到率; */
    private BigDecimal signInRate;
    /** 合格数量; */
    private Integer qualifiedNum;
    /** 报名人数; */
    private Integer signedUpNum;
    /** 平均参与时长(分钟); */
    private Integer avgParticipateTimeLength;
    /** 创建时间; */
    private Long createTime;
    /** 更新时间; */
    private Long updateTime;
    /** 起止时间 */
    private String activityStartEndTime;
    
    public static ActivityStatSummaryVO buildActivityStatSummaryVo(ActivityStatSummaryDTO activityStatSummaryDTO) {
        return ActivityStatSummaryVO.builder()
                .activityId(activityStatSummaryDTO.getActivityId())
                .signId(activityStatSummaryDTO.getSignId())
                .activityName(activityStatSummaryDTO.getActivityName())
                .activityPreviewUrl(activityStatSummaryDTO.getActivityPreviewUrl())
                .activityStatus(activityStatSummaryDTO.getActivityStatus())
                .activityClassify(activityStatSummaryDTO.getActivityClassify())
                .activityClassifyId(activityStatSummaryDTO.getActivityClassifyId())
                .activityCreator(activityStatSummaryDTO.getActivityCreator())
                .activityCreateUid(activityStatSummaryDTO.getActivityCreateUid())
                .participateScope(activityStatSummaryDTO.getParticipateScope())
                .integral(activityStatSummaryDTO.getIntegral())
                .startTime(Optional.ofNullable(activityStatSummaryDTO.getStartTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .endTime(Optional.ofNullable(activityStatSummaryDTO.getEndTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .rateNum(activityStatSummaryDTO.getRateNum())
                .rateScore(activityStatSummaryDTO.getRateScore())
                .signedInNum(activityStatSummaryDTO.getSignedInNum())
                .signInRate(activityStatSummaryDTO.getSignInRate())
                .qualifiedNum(activityStatSummaryDTO.getQualifiedNum())
                .signedUpNum(activityStatSummaryDTO.getSignedUpNum())
                .avgParticipateTimeLength(activityStatSummaryDTO.getAvgParticipateTimeLength())
                .createTime(Optional.ofNullable(activityStatSummaryDTO.getCreateTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .updateTime(Optional.ofNullable(activityStatSummaryDTO.getUpdateTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .activityStartEndTime(activityStatSummaryDTO.getActivityStartEndTime())
                .build();
    }
}
