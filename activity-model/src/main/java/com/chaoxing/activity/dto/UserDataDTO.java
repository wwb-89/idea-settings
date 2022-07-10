package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**用户活动数据
 * @author wwb
 * @version ver 1.0
 * @className UserDataDTO
 * @description 需要推送到三方的用户数据
 * @blame wwb
 * @date 2021-11-02 15:09:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDTO {

    /** uid */
    private Integer uid;
    /** 姓名 */
    private String userName;
    /** 用户名 */
    private String uname;
    /** 组织架构 */
    private String organization;
    /** 活动id */
    private Integer activityId;
    /** 活动名称 */
    private String activityName;
    /** 活动分类名 */
    private String activityClassifyName;
    /** 活动设定的积分 */
    private BigDecimal activityIntegral;
    /** 报名状态 */
    private String signUpStatus;
    /** 签到次数 */
    private Integer signedInNum;
    /** 签到请假次数 */
    private Integer signInLeaveNum;
    /** 未签次数 */
    private Integer unSignedInNum;
    /** 签到率 */
    private BigDecimal signInRate;
    /** 评价状态 */
    private String ratingStatus;
    /** 组织者审核状态 */
    private String auditStatus;
    /** 获得时间 */
    private LocalDateTime getTime;
    /** 积分编号 */
    private String integralNo;
    /** 参与时长 */
    private Integer participationDuration;
    /** 活动开始时间 */
    private LocalDateTime activityStartTime;
    /** 活动结束时间 */
    private LocalDateTime activityEndTime;

}