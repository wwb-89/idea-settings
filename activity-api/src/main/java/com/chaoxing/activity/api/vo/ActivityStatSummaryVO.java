package com.chaoxing.activity.api.vo;

import com.chaoxing.activity.dto.manager.sign.SignDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private String activityStatus;
    /** 活动分类; */
    private String activityClassify;
    /** 创建者; */
    private String activityCreator;
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
    /** 起止时间 */
    private String activityStartEndTime;
    /** 封面地址 */
    private String coverUrl;
    /** 活动简介 */
    private String introduction;
    /** 活动地址 */
    private String address;
    /** 活动总名额 */
    private Integer personLimit;
    /** 浏览数 */
    private Integer pv;
    /** 收藏数 */
    private Integer collectNum;
    /** 是否开启报名 */
    private Boolean openSignUp;
    /** 去报名地址 */
    private String signUpUrl;
    /** 创建机构fid */
    private Integer createFid;
    /** 创建机构名称 */
    private String createOrgName;
    /** 主办方 */
    private String organisers;
    /** 报名开始时间 */
    private Long signUpStartTime;
    /** 报名结束时间 */
    private Long signUpEndTime;
    /** 报名起止时间 */
    private String signUpStartEndTime;
    /** 发布时间 */
    private Long releaseTime;


    public static ActivityStatSummaryVO buildActivityStatSummaryVo(ActivityStatSummaryDTO actStatSummary) {
        Integer personLimit = null;
        if (actStatSummary.getSignUp() != null) {
            personLimit  = actStatSummary.getSignUp().getPersonLimit();
        }
        SignUpCreateParamDTO signUp = actStatSummary.getSignUp();
        String signUpUrl = "";
        String signUpStartEndTime = "";
        if (signUp != null && actStatSummary.getSignId() != null) {
            signUpUrl = SignDTO.getToSignUpUrl(actStatSummary.getSignId());
            signUpStartEndTime = DateUtils.activityTimeScope(DateUtils.timestamp2Date(signUp.getStartTime()), DateUtils.timestamp2Date(signUp.getEndTime()));
        }
        return ActivityStatSummaryVO.builder()
                .activityId(actStatSummary.getActivityId())
                .signId(actStatSummary.getSignId())
                .activityName(actStatSummary.getActivityName())
                .activityPreviewUrl(actStatSummary.getActivityPreviewUrl())
                .activityStatus(Activity.StatusEnum.fromValue(actStatSummary.getActivityStatus()).getName())
                .activityClassify(actStatSummary.getActivityClassify())
                .activityCreator(actStatSummary.getActivityCreator())
                .participateScope(actStatSummary.getParticipateScope())
                .integral(actStatSummary.getIntegral())
                .startTime(DateUtils.date2Timestamp(actStatSummary.getStartTime()))
                .endTime(DateUtils.date2Timestamp(actStatSummary.getEndTime()))
                .rateNum(actStatSummary.getRateNum())
                .rateScore(actStatSummary.getRateScore())
                .signedInNum(actStatSummary.getSignedInNum())
                .signInRate(actStatSummary.getSignInRate())
                .qualifiedNum(actStatSummary.getQualifiedNum())
                .signedUpNum(actStatSummary.getSignedUpNum())
                .avgParticipateTimeLength(actStatSummary.getAvgParticipateTimeLength())
                .activityStartEndTime(actStatSummary.getActivityStartEndTime())
                .coverUrl(actStatSummary.getCoverUrl())
                .introduction(actStatSummary.getIntroduction())
                .address(Optional.ofNullable(actStatSummary.getAddress()).orElse("").concat(Optional.ofNullable(actStatSummary.getDetailAddress()).orElse("")))
                .personLimit(personLimit)
                .openSignUp(signUp != null)
                .signUpUrl(signUpUrl)
                .collectNum(actStatSummary.getCollectNum())
                .pv(actStatSummary.getPv())
                .createFid(actStatSummary.getCreateFid())
                .createOrgName(actStatSummary.getCreateOrgName())
                .organisers(actStatSummary.getOrganisers())
                .signUpStartTime(Optional.ofNullable(signUp).map(SignUpCreateParamDTO::getStartTime).orElse(null))
                .signUpEndTime(Optional.ofNullable(signUp).map(SignUpCreateParamDTO::getEndTime).orElse(null))
                .signUpStartEndTime(signUpStartEndTime)
                .releaseTime(DateUtils.date2Timestamp(actStatSummary.getReleaseTime()))
                .build();
    }
}
