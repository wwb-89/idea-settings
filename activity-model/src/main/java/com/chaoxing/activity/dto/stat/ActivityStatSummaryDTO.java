package com.chaoxing.activity.dto.stat;

import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 2:11 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatSummaryDTO {

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
    private LocalDateTime startTime;
    /** 结束时间; */
    private LocalDateTime endTime;
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
    private LocalDateTime createTime;
    /** 更新时间; */
    private LocalDateTime updateTime;
    /** 起止时间 */
    private String activityStartEndTime;
    /** 封面地址 */
    private String coverUrl;
    /** 活动简介 */
    private String introduction;
    /** 活动地址 */
    private String address;
    /** 活动详细地址 */
    private String detailAddress;
    /** 活动下的报名(暂定第一个报名) */
    private SignUpCreateParamDTO signUp;
    /** 收藏数 */
    private Integer collectNum;
    /** 浏览数 */
    private Integer pv;
    /** 创建机构fid */
    private Integer createFid;
    /** 创建机构名称 */
    private String createOrgName;
    /** 主办方 */
    private String organisers;

}
