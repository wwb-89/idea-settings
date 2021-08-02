package com.chaoxing.activity.api.vo;

import com.chaoxing.activity.model.UserStatSummary;
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
public class UserStatSummaryVO {

    /** 用户uid; column: uid*/
    private Integer uid;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 登录名; column: uname*/
    private String uname;
    /** 真实姓名; column: real_name*/
    private String realName;
    /** 手机号; column: mobile*/
    private String mobile;
    /** 学号; column: student_no*/
    private String studentNo;
    /** 组织架构; column: organization_structure*/
    private String organizationStructure;
    /** 报名数量; column: sign_up_num*/
    private Integer signUpNum;
    /** 报名成功数量; column: signed_up_num*/
    private Integer signedUpNum;
    /** 报名时间; column: sign_up_time*/
    private Long signUpTime;
    /** 签到数量; column: sign_in_num*/
    private Integer signInNum;
    /** 签到成功数量; column: signed_in_num*/
    private Integer signedInNum;
    /** 评价数量; column: rating_num*/
    private Integer ratingNum;
    /** 获得的积分; column: integral*/
    private BigDecimal integral;
    /** 参与时长; column: participate_time_length*/
    private Integer participateTimeLength;
    /** 是否可用; column: is_valid*/
    private Boolean valid;
    /** 创建时间; column: create_time*/
    private Long createTime;
    /** 更新时间; column: update_time*/
    private Long updateTime;

    // 附加
    /** 是否合格 */
    private Boolean qualified;
    /** 合格数量（汇总多个活动时使用） */
    private Integer qualifiedNum;
    /** 参与活动数量 */
    private Integer participateActivityNum;
    /** 签到率 */
    private BigDecimal signedInRate;

    public static UserStatSummaryVO buildUserStatSummaryVO(UserStatSummary userStatSummary) {
        return UserStatSummaryVO.builder()
                .uid(userStatSummary.getUid())
                .activityId(userStatSummary.getActivityId())
                .uname(userStatSummary.getUname())
                .realName(userStatSummary.getRealName())
                .mobile(userStatSummary.getMobile())
                .studentNo(userStatSummary.getStudentNo())
                .organizationStructure(userStatSummary.getOrganizationStructure())
                .signUpNum(userStatSummary.getSignUpNum())
                .signedUpNum(userStatSummary.getSignedUpNum())
                .signUpTime(Optional.ofNullable(userStatSummary.getSignUpTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .signInNum(userStatSummary.getSignInNum())
                .signedInNum(userStatSummary.getSignedInNum())
                .ratingNum(userStatSummary.getRatingNum())
                .integral(userStatSummary.getIntegral())
                .participateTimeLength(userStatSummary.getParticipateTimeLength())
                .valid(userStatSummary.getValid())
                .createTime(Optional.ofNullable(userStatSummary.getCreateTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .updateTime(Optional.ofNullable(userStatSummary.getUpdateTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .qualified(userStatSummary.getQualified())
                .qualifiedNum(userStatSummary.getQualifiedNum())
                .participateActivityNum(userStatSummary.getParticipateActivityNum())
                .signedInRate(userStatSummary.getSignedInRate())
                .build();
    }

}
