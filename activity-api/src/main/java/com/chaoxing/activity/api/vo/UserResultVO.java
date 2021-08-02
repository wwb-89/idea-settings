package com.chaoxing.activity.api.vo;

import com.chaoxing.activity.dto.UserResultDTO;
import com.chaoxing.activity.model.UserResult;
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
 * @date 2021/8/2 19:49
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResultVO {
    /** 用户id */
    private Integer uid;

    /** 用户真实姓名 */
    private String realName;

    /** 学号 */
    private String studentNo;

    /** 手机号 */
    private String mobile;

    /** 活动id */
    private Integer activityId;

    /** 签到数 */
    private Integer signedInNum;

    /** 签到率 */
    private BigDecimal signedInRate;

    /** 评论数 */
    private Integer ratingNum;

    /** 总分(积分) */
    private Integer totalScore;

    /** 奖项 */
    private String prize;

    /** 状态: 0：不合格，1：合格，2：待处理 */
    private String qualifiedStatus;

    public static UserResultVO buildUserResult(UserResultDTO userResultDTO) {
        String status = Optional.ofNullable(UserResult.QualifiedStatusEnum.fromValue(userResultDTO.getQualifiedStatus())).map(UserResult.QualifiedStatusEnum::getName).orElse("待处理");
        return UserResultVO.builder()
                .uid(userResultDTO.getUid())
                .realName(userResultDTO.getRealName())
                .studentNo(userResultDTO.getStudentNo())
                .mobile(userResultDTO.getMobile())
                .activityId(userResultDTO.getActivityId())
                .signedInNum(userResultDTO.getSignedInNum())
                .signedInRate(userResultDTO.getSignedInRate())
                .ratingNum(userResultDTO.getRatingNum())
                .totalScore(userResultDTO.getTotalScore())
                .prize(userResultDTO.getPrize())
                .qualifiedStatus(status)
                .build();
    }
}
